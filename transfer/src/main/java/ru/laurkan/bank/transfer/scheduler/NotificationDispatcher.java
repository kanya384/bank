package ru.laurkan.bank.transfer.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.laurkan.bank.events.common.TransactionEventStatus;
import ru.laurkan.bank.events.transfer.TransferEvent;
import ru.laurkan.bank.events.transfer.TransferEventType;
import ru.laurkan.bank.transfer.exception.SendEventException;
import ru.laurkan.bank.transfer.exception.UnknownTransactionTypeException;
import ru.laurkan.bank.transfer.mapper.TransactionMapper;
import ru.laurkan.bank.transfer.model.SelfTransferTransaction;
import ru.laurkan.bank.transfer.model.Transaction;
import ru.laurkan.bank.transfer.model.TransactionStatus;
import ru.laurkan.bank.transfer.model.TransferToOtherUserTransaction;
import ru.laurkan.bank.transfer.repository.TransactionRepository;
import ru.laurkan.bank.transfer.repository.dto.TransactionRepositoryDTO;

import java.util.List;

import static ru.laurkan.bank.transfer.configuration.KafkaConfiguration.OUTPUT_TRANSFER_NOTIFICATION_EVENTS_TOPIC;

@Component
@RequiredArgsConstructor
public class NotificationDispatcher {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final KafkaTemplate<Long, TransferEvent> notificationsTemplate;

    @Scheduled(fixedDelay = 3000)
    public Flux<TransactionRepositoryDTO> sendNotifications() {
        return transactionRepository.findByTransactionStatusInAndNotificationSent(
                        List.of(TransactionStatus.BLOCKED, TransactionStatus.COMPLETED, TransactionStatus.FAILED,
                                TransactionStatus.NOT_ENOUGH_MONEY), false)
                .map(transactionMapper::map)
                .doOnNext(transaction -> {
                    TransferEvent transferEvent = eventFromTransaction(transaction);

                    try {
                        notificationsTemplate.send(OUTPUT_TRANSFER_NOTIFICATION_EVENTS_TOPIC,
                                transaction.getAccountId(), transferEvent).get();
                    } catch (Exception e) {
                        throw new SendEventException(e.getMessage());
                    }
                })
                .map(transaction -> {
                    transaction.setNotificationSent(true);
                    return transaction;
                })
                .map(transactionMapper::mapToDb)
                .flatMap(transactionRepository::save);
    }

    private TransferEvent eventFromTransaction(Transaction transaction) {
        return switch (transaction) {
            case SelfTransferTransaction selfTransferTransaction -> new TransferEvent(TransferEventType.SELF_TRANSFER,
                    selfTransferTransaction.getAccountId(),
                    selfTransferTransaction.getAmount(),
                    selfTransferTransaction.getReceiverAccountId(),
                    TransactionEventStatus
                            .valueOf(selfTransferTransaction.getTransactionStatus().toString()));
            case TransferToOtherUserTransaction transferToOtherUserTransaction ->
                    new TransferEvent(TransferEventType.SELF_TRANSFER,
                            transferToOtherUserTransaction.getAccountId(),
                            transferToOtherUserTransaction.getAmount(),
                            transferToOtherUserTransaction.getReceiverAccountId(),
                            TransactionEventStatus
                                    .valueOf(transferToOtherUserTransaction.getTransactionStatus().toString()));
            default -> throw new UnknownTransactionTypeException("unknown type of transaction");
        };
    }
}
