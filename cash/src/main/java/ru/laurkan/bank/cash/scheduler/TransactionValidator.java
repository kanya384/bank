package ru.laurkan.bank.cash.scheduler;

import lombok.RequiredArgsConstructor;
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

    private Mono<DecisionResponse> validate(Transaction transaction) {
        return switch (transaction) {
            case DepositTransaction depositTransaction ->
                    blockerClient.validate(transactionMapper.map(depositTransaction));
            case WithdrawalTransaction withdrawalTransaction ->
                    blockerClient.validate(transactionMapper.map(withdrawalTransaction));
            default -> throw new IllegalStateException("Unexpected value: " + transaction);
        };
    }
}
