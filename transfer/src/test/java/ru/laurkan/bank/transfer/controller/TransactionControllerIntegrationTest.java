package ru.laurkan.bank.transfer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.laurkan.bank.transfer.AbstractTestContainer;
import ru.laurkan.bank.transfer.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.transfer.repository.TransactionRepository;

@AutoConfigureWebTestClient
@DirtiesContext
public class TransactionControllerIntegrationTest extends AbstractTestContainer {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void create_shouldCreateSelfTransferTransaction() {
        var request = new CreateTransactionRequestDTO();
        request.setAccountId(5L);
        request.setAmount(10.0);
        request.setReceiverAccountId(6L);

        webTestClient
                .post()
                .uri("/transaction/self_transfer")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNumber()
                .jsonPath("$.accountId").isEqualTo(5L)
                .jsonPath("$.amount").isEqualTo(10)
                .jsonPath("$.receiverAccountId").isEqualTo(6L)
                .jsonPath("$.transactionType").isEqualTo("SELF_TRANSFER");
    }

    @Test
    public void create_shouldCreateTransferToOtherUserTransaction() {
        var request = new CreateTransactionRequestDTO();
        request.setAccountId(5L);
        request.setAmount(10.0);
        request.setReceiverAccountId(6L);

        webTestClient
                .post()
                .uri("/transaction/transfer_to_other_user")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNumber()
                .jsonPath("$.accountId").isEqualTo(5L)
                .jsonPath("$.amount").isEqualTo(10)
                .jsonPath("$.receiverAccountId").isEqualTo(6L)
                .jsonPath("$.transactionType").isEqualTo("TRANSFER_TO_OTHER_USER");
    }
}
