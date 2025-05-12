package ru.laurkan.bank.transfer.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.accounts.AccountsClient;
import ru.laurkan.bank.clients.accounts.dto.accounts.TransferMoneyRequest;
import ru.laurkan.bank.clients.accounts.dto.accounts.TransferMoneyResponse;
import ru.laurkan.bank.clients.accounts.exception.MoneyException;
import ru.laurkan.bank.clients.blocker.BlockerClient;
import ru.laurkan.bank.clients.blocker.dto.DecisionResponse;
import ru.laurkan.bank.clients.exchange.ExchangeClient;
import ru.laurkan.bank.clients.exchange.dto.Currency;
import ru.laurkan.bank.clients.exchange.dto.ExchangeRateResponse;
import ru.laurkan.bank.clients.notification.NotificationClient;
import ru.laurkan.bank.transfer.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.transfer.dto.TransactionResponseDTO;
import ru.laurkan.bank.transfer.exception.UnknownTransactionTypeException;
import ru.laurkan.bank.transfer.mapper.NotificationMapper;
import ru.laurkan.bank.transfer.mapper.TransactionMapper;
import ru.laurkan.bank.transfer.model.*;
import ru.laurkan.bank.transfer.repository.TransactionRepository;
import ru.laurkan.bank.transfer.service.TransactionService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountsClient accountsClient;
    private final BlockerClient blockerClient;
    private final NotificationClient notificationClient;
    private final NotificationMapper notificationMapper;
    private final ExchangeClient exchangeClient;


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
                        List.of(TransactionStatus.BLOCKED, TransactionStatus.COMPLETED, TransactionStatus.FAILED,
                                TransactionStatus.NOT_ENOUGH_MONEY), false)
                .map(transactionMapper::map)
                .flatMap(this::sendEmail);
    }

    private Mono<DecisionResponse> validate(Transaction transaction) {
        return switch (transaction) {
            case SelfTransferTransaction selfTransferTransaction ->
                    blockerClient.validate(transactionMapper.map(selfTransferTransaction));
            case TransferToOtherUserTransaction transferToOtherUserTransaction ->
                    blockerClient.validate(transactionMapper.map(transferToOtherUserTransaction));
            default -> throw new UnknownTransactionTypeException("Unexpected value: "
                    + transaction.getClass().getSimpleName());
        };
    }

    private Mono<TransferMoneyResponse> processTransaction(Transaction transaction) {
        return exchangeClient.readAll()
                .collectMap(ExchangeRateResponse::getCurrency, ExchangeRateResponse::getRate)
                .zipWith(readCurrenciesOfAccounts(transaction))
                .flatMap(tuple -> {
                    var rates = tuple.getT1();
                    var currencies = tuple.getT2();
                    return accountsClient.transferMoney(TransferMoneyRequest.builder()
                            .fromAccountId(transaction.getAccountId())
                            .fromMoneyAmount(transaction.getAmount())
                            .toAccountId(transaction.getReceiverAccountId())
                            .toMoneyAmount(exchangeMoney(transaction.getAmount(), currencies.getFirst(), currencies.getLast(), rates))
                            .build());
                });
    }

    private Mono<List<Currency>> readCurrenciesOfAccounts(Transaction transaction) {
        return accountsClient.readAccountById(transaction.getAccountId())
                .zipWith(accountsClient.readAccountById(transaction.getReceiverAccountId()))
                .map(tuple ->
                        List.of(Currency.valueOf(tuple.getT1().getCurrency()), Currency.valueOf(tuple.getT2().getCurrency())));
    }

    private Double exchangeMoney(Double amount, Currency from, Currency to, Map<Currency, Double> rates) {
        Double amountInRubles = amount / rates.get(from);
        return amountInRubles * rates.get(to);
    }

    private Mono<Transaction> sendEmail(Transaction transaction) {
        return accountsClient.findUserByAccountId(transaction.getAccountId())
                .map(userResponse -> notificationMapper.map(transaction, userResponse.getEmail(),
                        userResponse.getSurname() + " " + userResponse.getName()))
                .flatMap(notificationClient::sendEmailNotification)
                .flatMap(__ -> {
                    transaction.setNotificationSent(true);
                    return transactionRepository.save(transactionMapper.mapToDb(transaction));
                })
                .map(transactionMapper::map);
    }
}
