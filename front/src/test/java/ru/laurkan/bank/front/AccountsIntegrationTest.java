package ru.laurkan.bank.front;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.laurkan.bank.front.model.User;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(
        ids = "ru.laurkan:accounts:+:stubs:9000",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class AccountsIntegrationTest {
    private final String BASE_URL = "http://localhost:";

    @LocalServerPort
    int port;

    @Autowired
    private WebTestClient webTestClient;

    private final User mockUser1 = User.builder()
            .id(1L)
            .name("user")
            .login("user")
            .password("password")
            .email("email1@mail.ru")
            .birth(LocalDate.of(1990, 6, 20))
            .build();

    private final User mockUser2 = User.builder()
            .id(2L)
            .name("user2")
            .login("user2")
            .password("password")
            .email("email2@mail.ru")
            .birth(LocalDate.of(1990, 6, 20))
            .build();

    @Test
    void readAccountsOfUser_shouldReturnAccountsOfUser() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(mockUser2))
                .get()
                .uri(BASE_URL + port + "/")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody()
                .xpath("//table/tbody/tr").nodeCount(3);
    }

    @Test
    void createAccount_shouldCreateAccountForUser() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(mockUser1))
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(BASE_URL + port + "/accounts")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("userId", "1")
                        .with("currency", "USD")
                )
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void deleteAccount_shouldDeleteAccountOfUser() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(mockUser1))
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(BASE_URL + port + "/accounts/1/delete")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .exchange()
                .expectStatus().is3xxRedirection();
    }
}
