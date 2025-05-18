package ru.laurkan.bank.cash.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.laurkan.bank.cash.AbstractTestContainer;
import ru.laurkan.bank.cash.model.TransactionStatus;
import ru.laurkan.bank.cash.model.TransactionType;
import ru.laurkan.bank.cash.repository.dto.TransactionRepositoryDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
public class TransactionRepositoryIntegrationTest extends AbstractTestContainer {
    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        var count = transactionRepository.count().block();
        if (count != null && count == 0) {
            var deposit = TransactionRepositoryDTO.builder()
                    .accountId(1L)
                    .amount(10.0)
                    .transactionStatus(TransactionStatus.CREATED)
                    .transactionType(TransactionType.DEPOSIT)
                    .notificationSent(false)
                    .build();
            transactionRepository.save(deposit).block();

            var withdrawalCompleted = TransactionRepositoryDTO.builder()
                    .accountId(1L)
                    .amount(10.0)
                    .transactionStatus(TransactionStatus.COMPLETED)
                    .transactionType(TransactionType.WITHDRAWAL)
                    .notificationSent(true)
                    .build();
            transactionRepository.save(withdrawalCompleted).block();

            var withdrawalBlocked = TransactionRepositoryDTO.builder()
                    .accountId(1L)
                    .amount(10.0)
                    .transactionStatus(TransactionStatus.BLOCKED)
                    .transactionType(TransactionType.WITHDRAWAL)
                    .notificationSent(false)
                    .build();
            transactionRepository.save(withdrawalBlocked).block();
        }
    }

    @Test
    public void findByTransactionStatus_shouldFindByTransactionStatus() {
        StepVerifier.create(transactionRepository.findByTransactionStatus(TransactionStatus.CREATED)
                        .collectList())
                .assertNext(dtos -> assertThat(dtos)
                        .hasSize(1)
                        .first()
                        .extracting(TransactionRepositoryDTO::getTransactionStatus)
                        .isEqualTo(TransactionStatus.CREATED))
                .verifyComplete();
    }

    @Test
    public void findByTransactionStatusInAndNotificationSent_shouldReturnTransaction() {
        StepVerifier.create(transactionRepository.findByTransactionStatusInAndNotificationSent(
                                List.of(TransactionStatus.COMPLETED), true)
                        .collectList())
                .assertNext(dtos -> assertThat(dtos)
                        .hasSize(1)
                        .first()
                        .extracting(TransactionRepositoryDTO::getTransactionStatus)
                        .isEqualTo(TransactionStatus.COMPLETED))
                .verifyComplete();
    }
}
