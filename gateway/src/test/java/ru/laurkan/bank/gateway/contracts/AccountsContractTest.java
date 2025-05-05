package ru.laurkan.bank.gateway.contracts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(
        ids = "ru.laurkan:accounts:+:stubs:9000",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class AccountsContractTest {
    private final String BASE_URL = "http://localhost:9000";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void signIn_shouldAuthenticateUser() {
        /*var signInRequest = SignInRequestDTO.builder()
                .login("user")
                .password("password")
                .build();

        webTestClient.post()
                .uri(BASE_URL + "/user/find-by-credentials")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signInRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("{\"userExists\":true}"));
                });*/
    }

    @Test
    void signUp_shouldRegisterUser() {
        /*webTestClient.post()
                .uri(BASE_URL + "/user/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"login\":\"user\",\"password\":\"password\"}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("{\"userExists\":true}"));
                });*/
    }
}
