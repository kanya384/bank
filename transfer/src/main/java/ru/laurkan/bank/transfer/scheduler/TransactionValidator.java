package ru.laurkan.bank.transfer.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.blocker.BlockerClient;
import ru.laurkan.bank.clients.blocker.dto.DecisionResponse;
import ru.laurkan.bank.transfer.exception.UnknownTransactionTypeException;
import ru.laurkan.bank.transfer.mapper.TransactionMapper;
import ru.laurkan.bank.transfer.model.SelfTransferTransaction;
import ru.laurkan.bank.transfer.model.Transaction;
import ru.laurkan.bank.transfer.model.TransactionStatus;
import ru.laurkan.bank.transfer.model.TransferToOtherUserTransaction;
import ru.laurkan.bank.transfer.repository.TransactionRepository;

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
            case SelfTransferTransaction selfTransferTransaction ->
                    blockerClient.validate(transactionMapper.map(selfTransferTransaction));
            case TransferToOtherUserTransaction transferToOtherUserTransaction ->
                    blockerClient.validate(transactionMapper.map(transferToOtherUserTransaction));
            default -> throw new UnknownTransactionTypeException("Unexpected value: "
                    + transaction.getClass().getSimpleName());
        };
    }
}
