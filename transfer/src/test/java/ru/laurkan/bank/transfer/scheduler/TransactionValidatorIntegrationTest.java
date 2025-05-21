package ru.laurkan.bank.transfer.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import ru.laurkan.bank.transfer.AbstractTestContainer;
import ru.laurkan.bank.transfer.model.Transaction;
import ru.laurkan.bank.transfer.model.TransactionStatus;
import ru.laurkan.bank.transfer.model.TransactionType;
import ru.laurkan.bank.transfer.repository.TransactionRepository;
import ru.laurkan.bank.transfer.repository.dto.TransactionRepositoryDTO;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureStubRunner(
        ids = "ru.laurkan:blocker:+:stubs:9010",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@ActiveProfiles("test")
public class TransactionValidatorIntegrationTest extends AbstractTestContainer {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionValidator transactionValidator;

    @BeforeEach
    public void setUp() {
        var count = transactionRepository.count().block();
        if (count != null && count == 0) {
            var transaction = new TransactionRepositoryDTO();
            transaction.setAccountId(1L);
            transaction.setAmount(100.0);
            transaction.setReceiverAccountId(2L);
            transaction.setTransactionType(TransactionType.SELF_TRANSFER);
            transaction.setTransactionStatus(TransactionStatus.CREATED);
            transaction.setNotificationSent(false);

            transactionRepository.save(transaction)
                    .block();

            //update date for contract test
            transaction.setCreatedAt(LocalDateTime.of(2025, 8, 9, 10, 30));
            transaction.setModifiedAt(LocalDateTime.of(2025, 8, 9, 10, 30));

            transactionRepository.save(transaction)
                    .block();
        }
    }

    @Test
    public void validateCreatedTransactions_shouldValidateTransaction() {
        StepVerifier.create(transactionValidator.validateCreatedTransactions()
                        .collectList()
                )
                .assertNext(validatedList -> assertThat(validatedList)
                        .hasSize(1)
                        .first()
                        .extracting(Transaction::getTransactionStatus)
                        .isEqualTo(TransactionStatus.APPROVED))
                .verifyComplete();
    }

}
