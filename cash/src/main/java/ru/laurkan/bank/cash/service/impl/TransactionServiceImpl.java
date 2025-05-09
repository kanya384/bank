package ru.laurkan.bank.cash.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.cash.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.cash.dto.TransactionResponseDTO;
import ru.laurkan.bank.cash.mapper.NotificationMapper;
import ru.laurkan.bank.cash.mapper.TransactionMapper;
import ru.laurkan.bank.cash.model.*;
import ru.laurkan.bank.cash.repository.TransactionRepository;
import ru.laurkan.bank.cash.service.TransactionService;
import ru.laurkan.bank.clients.accounts.AccountsClient;
import ru.laurkan.bank.clients.accounts.dto.accounts.AccountResponse;
import ru.laurkan.bank.clients.accounts.dto.accounts.PutMoneyToAccount;
import ru.laurkan.bank.clients.accounts.dto.accounts.TakeMoneyFromAccount;
import ru.laurkan.bank.clients.accounts.exception.MoneyException;
import ru.laurkan.bank.clients.blocker.BlockerClient;
import ru.laurkan.bank.clients.blocker.dto.DecisionResponse;
import ru.laurkan.bank.clients.notification.NotificationClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountsClient accountsClient;
    private final BlockerClient blockerClient;
    private final NotificationClient notificationClient;
    private final NotificationMapper notificationMapper;

    @Autowired
    public TransactionServiceImpl(@Value("${clients.accounts.uri}") String accountUri,
                                  @Value("${clients.blocker.uri}") String blockerUri,
                                  @Value("${clients.notifications.uri}") String notificationsUri,
                                  WebClient webClient,
                                  TransactionRepository transactionRepository,
                                  TransactionMapper transactionMapper,
                                  NotificationMapper notificationMapper
    ) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.accountsClient = new AccountsClient(accountUri, webClient);
        this.blockerClient = new BlockerClient(blockerUri, webClient);
        this.notificationClient = new NotificationClient(notificationsUri, webClient);
        this.notificationMapper = notificationMapper;
    }


    @Override
    public Mono<TransactionResponseDTO> create(TransactionType transactionType, CreateTransactionRequestDTO request) {
        var transaction = transactionMapper.map(transactionType, request);
        return Mono.just(transaction)
                .map(transactionMapper::mapToDb)
                .flatMap(transactionRepository::save)
                .map(transactionMapper::map)
                .map(transactionMapper::map);
    }

    @Scheduled(fixedDelay = 3000)
    @Override
    public Flux<Transaction> validateCreatedTransactions() {
        return transactionRepository
                .findByTransactionStatus(TransactionStatus.CREATED)
                .map(transactionMapper::map)
                .flatMap(transaction -> validate(transaction)
                        .flatMap(decisionResponse -> {
                            transaction.setTransactionStatus(decisionResponse.isBlocked() ?
                                    TransactionStatus.BLOCKED :
                                    TransactionStatus.APPROVED);
                            return transactionRepository.save(transactionMapper.mapToDb(transaction));
                        }))
                .map(transactionMapper::map)
                .onErrorResume(e -> {
                    //TODO - log error
                    return Mono.empty();
                });
    }

    @Scheduled(fixedDelay = 3000)
    @Override
    public Flux<Transaction> processApprovedTransactions() {
        return transactionRepository
                .findByTransactionStatus(TransactionStatus.APPROVED)
                .map(transactionMapper::map)
                .flatMap(transaction -> processTransaction(transaction)
                        .onErrorResume(e -> {
                            if (e instanceof MoneyException) {
                                transaction.setTransactionStatus(TransactionStatus.NOT_ENOUGH_MONEY);
                            } else {
                                transaction.setTransactionStatus(TransactionStatus.FAILED);
                            }

                            return transactionRepository.save(transactionMapper.mapToDb(transaction))
                                    .flatMap(__ -> Mono.error(e));
                        })
                        .flatMap(account -> {
                            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
                            return transactionRepository.save(transactionMapper.mapToDb(transaction));
                        })
                )
                .map(transactionMapper::map);
    }

    @Scheduled(fixedDelay = 3000)
    @Override
    public Flux<Transaction> sendNotifications() {
        return transactionRepository.findByTransactionStatusInAndNotificationSent(
                        List.of(TransactionStatus.COMPLETED, TransactionStatus.FAILED, TransactionStatus.NOT_ENOUGH_MONEY), false)
                .map(transactionMapper::map)
                .flatMap(this::sendEmail);
    }

    private Mono<DecisionResponse> validate(Transaction transaction) {
        return switch (transaction) {
            case DepositTransaction depositTransaction ->
                    blockerClient.validate(transactionMapper.map(depositTransaction));
            case WithdrawalTransaction withdrawalTransaction ->
                    blockerClient.validate(transactionMapper.map(withdrawalTransaction));
            default -> throw new IllegalStateException("Unexpected value: " + transaction);
        };
    }

    private Mono<AccountResponse> processTransaction(Transaction transaction) {
        return switch (transaction) {
            case DepositTransaction depositTransaction -> accountsClient.putMoneyToAccount(transaction.getAccountId(),
                    PutMoneyToAccount.builder()
                            .amount(transaction.getAmount())
                            .build());
            case WithdrawalTransaction withdrawalTransaction ->
                    accountsClient.takeMoneyFromAccount(transaction.getAccountId(),
                            TakeMoneyFromAccount.builder()
                                    .amount(transaction.getAmount())
                                    .build());
            default -> throw new IllegalStateException("Unexpected value: " + transaction);
        };
    }

    private Mono<Transaction> sendEmail(Transaction transaction) {
        return accountsClient.findUserByAccountId(transaction.getAccountId())
                .map(userResponse -> notificationMapper.map(transaction, userResponse.getEmail()))
                .flatMap(notificationClient::sendEmailNotification)
                .flatMap(__ -> {
                    transaction.setNotificationSent(true);
                    return transactionRepository.save(transactionMapper.mapToDb(transaction));
                })
                .map(transactionMapper::map);
    }
}
