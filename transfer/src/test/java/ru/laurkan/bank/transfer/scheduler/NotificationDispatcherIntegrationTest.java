package ru.laurkan.bank.transfer.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import ru.laurkan.bank.transfer.AbstractTestContainer;
import ru.laurkan.bank.transfer.model.TransactionStatus;
import ru.laurkan.bank.transfer.model.TransactionType;
import ru.laurkan.bank.transfer.repository.TransactionRepository;
import ru.laurkan.bank.transfer.repository.dto.TransactionRepositoryDTO;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureStubRunner(
        ids = {"ru.laurkan:accounts:+:stubs:9000", "ru.laurkan:notifications:+:stubs:9020"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@DirtiesContext
@ActiveProfiles("test")
public class NotificationDispatcherIntegrationTest extends AbstractTestContainer {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private NotificationDispatcher notificationDispatcher;

    @BeforeEach
    public void setUp() {
        var count = transactionRepository.count().block();
        if (count != null && count == 0) {
            var transaction = new TransactionRepositoryDTO();
            transaction.setAccountId(1L);
            transaction.setAmount(100.0);
            transaction.setReceiverAccountId(2L);
            transaction.setTransactionType(TransactionType.SELF_TRANSFER);
            transaction.setTransactionStatus(TransactionStatus.COMPLETED);
            transaction.setNotificationSent(false);

            transactionRepository.save(transaction)
                    .block();
        }
    }

    @Test
    public void sendNotifications_shouldSendNotifications() {
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
