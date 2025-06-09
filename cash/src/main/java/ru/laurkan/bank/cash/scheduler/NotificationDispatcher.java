package ru.laurkan.bank.cash.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.laurkan.bank.cash.exception.SendEventException;
import ru.laurkan.bank.cash.exception.UnsupportedTransactionType;
import ru.laurkan.bank.cash.mapper.TransactionMapper;
import ru.laurkan.bank.cash.model.DepositTransaction;
import ru.laurkan.bank.cash.model.Transaction;
import ru.laurkan.bank.cash.model.TransactionStatus;
import ru.laurkan.bank.cash.model.WithdrawalTransaction;
import ru.laurkan.bank.cash.repository.TransactionRepository;
import ru.laurkan.bank.cash.repository.dto.TransactionRepositoryDTO;
import ru.laurkan.bank.events.cash.CashEvent;
import ru.laurkan.bank.events.cash.CashEventType;
import ru.laurkan.bank.events.common.TransactionEventStatus;

import java.util.List;

import static ru.laurkan.bank.cash.configuration.KafkaConfiguration.OUTPUT_CASH_NOTIFICATION_EVENTS_TOPIC;

@Component
@RequiredArgsConstructor
public class NotificationDispatcher {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final KafkaTemplate<Long, CashEvent> notificationsTemplate;

    @Scheduled(fixedDelay = 3000)
    public Flux<TransactionRepositoryDTO> sendNotifications() {
        return transactionRepository.findByTransactionStatusInAndNotificationSent(
                        List.of(TransactionStatus.BLOCKED, TransactionStatus.COMPLETED,
                                TransactionStatus.FAILED, TransactionStatus.NOT_ENOUGH_MONEY), false)
                .map(transactionMapper::map)
                .doOnNext(transaction -> {
                    CashEvent cashEvent = cashEventFromTransaction(transaction);

                    try {
                        notificationsTemplate.send(OUTPUT_CASH_NOTIFICATION_EVENTS_TOPIC,
                                transaction.getAccountId(), cashEvent).get();
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

    private CashEvent cashEventFromTransaction(Transaction transaction) {
        return switch (transaction) {
            case DepositTransaction depositTransaction -> new CashEvent(CashEventType.DEPOSIT_TRANSACTION,
                    depositTransaction.getAccountId(), depositTransaction.getAmount(),
                    TransactionEventStatus
                            .valueOf(depositTransaction.getTransactionStatus().toString()));
            case WithdrawalTransaction withdrawalTransaction -> new CashEvent(CashEventType.WITHDRAWAL_TRANSACTION,
                    withdrawalTransaction.getAccountId(), withdrawalTransaction.getAmount(),
                    TransactionEventStatus
                            .valueOf(withdrawalTransaction.getTransactionStatus().toString()));
            default -> throw new UnsupportedTransactionType("unknown type of transaction");
        };
    }
}
