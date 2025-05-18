package ru.laurkan.bank.cash.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import ru.laurkan.bank.cash.AbstractTestContainer;
import ru.laurkan.bank.cash.model.Transaction;
import ru.laurkan.bank.cash.model.TransactionStatus;
import ru.laurkan.bank.cash.model.TransactionType;
import ru.laurkan.bank.cash.repository.TransactionRepository;
import ru.laurkan.bank.cash.repository.dto.TransactionRepositoryDTO;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureStubRunner(
        ids = {"ru.laurkan:accounts:+:stubs:9000"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@DirtiesContext
@ActiveProfiles("test")
public class TransactionDispatcherIntegrationTest extends AbstractTestContainer {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionDispatcher transactionDispatcher;

    @BeforeEach
    public void setUp() {
        var count = transactionRepository.count().block();
        if (count != null && count == 0) {
            var transaction = new TransactionRepositoryDTO();
            transaction.setAccountId(2L);
            transaction.setAmount(10.5);
            transaction.setTransactionType(TransactionType.DEPOSIT);
            transaction.setTransactionStatus(TransactionStatus.APPROVED);
            transaction.setNotificationSent(false);

            transactionRepository.save(transaction)
                    .block();
        }
    }

    @Test
    public void processApprovedTransactions_shouldProcessApprovedTransactions() {
        StepVerifier.create(transactionDispatcher.processApprovedTransactions()
                        .collectList()
                )
                .assertNext(validatedList -> assertThat(validatedList)
                        .hasSize(1)
                        .first()
                        .extracting(Transaction::getTransactionStatus)
                        .isEqualTo(TransactionStatus.COMPLETED))
                .verifyComplete();
    }
}
