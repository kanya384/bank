package ru.laurkan.bank.blocker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.laurkan.bank.blocker.SecurityTestConfiguration;
import ru.laurkan.bank.blocker.dto.DepositTransactionRequestDTO;
import ru.laurkan.bank.blocker.dto.SelfTransferTransactionRequestDTO;
import ru.laurkan.bank.blocker.dto.TransferToOtherUserTransactionRequestDTO;
import ru.laurkan.bank.blocker.dto.WithdrawalTransactionRequestDTO;

import java.time.LocalDateTime;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Import(SecurityTestConfiguration.class)
public class BlockerControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void transaction_shouldValidateDepositTransaction() {
        var request = new DepositTransactionRequestDTO();
        request.setAccountId(1L);
        request.setAmount(1000.0);
        request.setCreatedAt(LocalDateTime.now());

        webTestClient.post()
                .uri("/deposit")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.isBlocked").isNotEmpty();
    }

    @Test
    public void transaction_shouldValidateWithdrawalTransaction() {
        var request = new WithdrawalTransactionRequestDTO();
        request.setAccountId(1L);
        request.setAmount(1000.0);
        request.setCreatedAt(LocalDateTime.now());

        webTestClient.post()
                .uri("/withdrawal")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.isBlocked").isNotEmpty();
    }

    @Test
    public void transaction_shouldValidateSelfTransferTransaction() {
        var request = new SelfTransferTransactionRequestDTO();
        request.setAccountId(1L);
        request.setAmount(1000.0);
        request.setReceiverAccountId(2L);
        request.setCreatedAt(LocalDateTime.now());

        webTestClient.post()
                .uri("/transfer-self")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.isBlocked").isNotEmpty();
    }

    @Test
    public void transaction_shouldValidateTransferToOtherUserTransaction() {
        var request = new TransferToOtherUserTransactionRequestDTO();
        request.setAccountId(1L);
        request.setAmount(1000.0);
        request.setReceiverAccountId(2L);
        request.setCreatedAt(LocalDateTime.now());

        webTestClient.post()
                .uri("/transfer-to-other-user")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.isBlocked").isNotEmpty();
    }
}
