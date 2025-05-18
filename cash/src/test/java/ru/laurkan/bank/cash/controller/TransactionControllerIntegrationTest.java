package ru.laurkan.bank.cash.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.laurkan.bank.cash.AbstractTestContainer;
import ru.laurkan.bank.cash.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.cash.repository.TransactionRepository;

@AutoConfigureWebTestClient
@DirtiesContext
public class TransactionControllerIntegrationTest extends AbstractTestContainer {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void create_shouldCreateDepositTransaction() {
        var request = new CreateTransactionRequestDTO();
        request.setAccountId(1L);
        request.setAmount(10.0);

        webTestClient
                .post()
                .uri("/transaction/deposit")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNumber()
                .jsonPath("$.accountId").isEqualTo(1L)
                .jsonPath("$.amount").isEqualTo(10)
                .jsonPath("$.transactionType").isEqualTo("DEPOSIT");
    }

    @Test
    public void create_shouldCreateWithdrawalTransaction() {
        var request = new CreateTransactionRequestDTO();
        request.setAccountId(1L);
        request.setAmount(10.0);

        webTestClient
                .post()
                .uri("/transaction/withdrawal")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNumber()
                .jsonPath("$.accountId").isEqualTo(1L)
                .jsonPath("$.amount").isEqualTo(10)
                .jsonPath("$.transactionType").isEqualTo("WITHDRAWAL");
    }
}
