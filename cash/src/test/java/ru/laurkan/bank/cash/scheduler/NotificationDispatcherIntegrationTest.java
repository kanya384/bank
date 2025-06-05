package ru.laurkan.bank.cash.scheduler;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.test.StepVerifier;
import ru.laurkan.bank.cash.AbstractTestContainer;
import ru.laurkan.bank.cash.model.TransactionStatus;
import ru.laurkan.bank.cash.model.TransactionType;
import ru.laurkan.bank.cash.repository.TransactionRepository;
import ru.laurkan.bank.cash.repository.dto.TransactionRepositoryDTO;
import ru.laurkan.bank.events.cash.CashEvent;
import ru.laurkan.bank.events.cash.CashEventType;
import ru.laurkan.bank.events.common.TransactionEventStatus;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.laurkan.bank.cash.configuration.KafkaConfiguration.OUTPUT_CASH_NOTIFICATION_EVENTS_TOPIC;

@DirtiesContext
@ActiveProfiles("test")
public class NotificationDispatcherIntegrationTest extends AbstractTestContainer {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationDispatcher notificationDispatcher;

    @MockitoBean
    private KafkaTemplate<Long, CashEvent> notificationsTemplate;

    @BeforeEach
    public void setUp() {
        var count = transactionRepository.count().block();
        if (count != null && count == 0) {
            var transaction = new TransactionRepositoryDTO();
            transaction.setAccountId(1L);
            transaction.setAmount(100.0);
            transaction.setTransactionType(TransactionType.DEPOSIT);
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            transaction.setNotificationSent(false);
            transaction.setCreatedAt(LocalDateTime.now());

            transactionRepository.save(transaction)
                    .block();
        }
    }

    @Test
    public void sendNotifications_shouldSendNotifications() {
        when(notificationsTemplate.send(any(String.class), any(Long.class), any(CashEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(new SendResult<>(new ProducerRecord<>(OUTPUT_CASH_NOTIFICATION_EVENTS_TOPIC, 0,
                        1L,
                        new CashEvent(CashEventType.DEPOSIT_TRANSACTION, 1L, 100.0,
                                TransactionEventStatus.COMPLETED)),
                        new RecordMetadata(new TopicPartition(OUTPUT_CASH_NOTIFICATION_EVENTS_TOPIC, 0),
                                0L, 0, 0L, 0, 0))));

        StepVerifier.create(notificationDispatcher.sendNotifications()
                        .collectList()
                )
                .assertNext(validatedList -> assertThat(validatedList)
                        .hasSize(1)
                        .first()
                        .extracting(TransactionRepositoryDTO::getNotificationSent)
                        .isEqualTo(true))
                .verifyComplete();
    }
}
