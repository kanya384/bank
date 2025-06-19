package ru.laurkan.bank.cash.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.cash.mapper.TransactionMapper;
import ru.laurkan.bank.cash.model.DepositTransaction;
import ru.laurkan.bank.cash.model.Transaction;
import ru.laurkan.bank.cash.model.TransactionStatus;
import ru.laurkan.bank.cash.model.WithdrawalTransaction;
import ru.laurkan.bank.cash.repository.TransactionRepository;
import ru.laurkan.bank.clients.blocker.BlockerClient;
import ru.laurkan.bank.clients.blocker.dto.DecisionResponse;

@Log4j2
@Component
@RequiredArgsConstructor
public class TransactionValidator {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final BlockerClient blockerClient;

    @Scheduled(fixedDelay = 3000)
    public Flux<Transaction> validateCreatedTransactions() {
        return transactionRepository
                .findByTransactionStatus(TransactionStatus.CREATED)
                .map(transactionMapper::map)
                .flatMap(transaction -> validate(transaction)
                        .doOnError(e -> {
                            log.error("error request transaction validation ({}): {}",
                                    transaction, e.getMessage());
                        })
                        .flatMap(decisionResponse -> {
                            transaction.setTransactionStatus(decisionResponse.isBlocked() ?
                                    TransactionStatus.BLOCKED :
                                    TransactionStatus.APPROVED);
                            return transactionRepository.save(transactionMapper.mapToDb(transaction))
                                    .doOnError(e -> {
                                        log.error("error saving validated transaction ({}): {}", transaction,
                                                e.getMessage());
                                    });
                        }))
                .map(transactionMapper::map);
    }

    private Mono<DecisionResponse> validate(Transaction transaction) {
        log.debug("transaction validation started: {}", transaction);
        return switch (transaction) {
            case DepositTransaction depositTransaction ->
                    blockerClient.validate(transactionMapper.map(depositTransaction));
            case WithdrawalTransaction withdrawalTransaction ->
                    blockerClient.validate(transactionMapper.map(withdrawalTransaction));
            default -> throw new IllegalStateException("Unexpected value: " + transaction);
        };
    }
}
