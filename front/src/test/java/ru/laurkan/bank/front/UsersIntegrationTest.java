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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(
        ids = "ru.laurkan:accounts:+:stubs:9000",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class UsersIntegrationTest {
    private final String BASE_URL = "http://localhost:";

    @LocalServerPort
    int port;

    @Autowired
    private WebTestClient webTestClient;

    private final User mockUser = User.builder()
            .id(1L)
            .name("user")
            .login("user")
            .password("password")
            .email("email1@mail.ru")
            .birth(LocalDate.of(1990, 6, 20))
            .build();

    @Test
    void profile_shouldReturnProfilePage() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(mockUser))
                .get()
                .uri(BASE_URL + port + "/profile")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody()
                .xpath("//h5/span[contains(@class, 'login')]")
                .string(s -> assertEquals(s, mockUser.getLogin()));
    }

    @Test
    void register_shouldRegisterUser() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(mockUser))
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(BASE_URL + port + "/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("name", "username")
                        .with("surname", "surname")
                        .with("email", "test99@mail.ru")
                        .with("login", "login")
                        .with("password", "password")
                        .with("birth", "01.02.1990")
                )
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void register_shouldNotRegisterYoungUser() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(mockUser))
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(BASE_URL + port + "/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("name", "username")
                        .with("surname", "surname")
                        .with("email", "test99@mail.ru")
                        .with("login", "login")
                        .with("password", "password")
                        .with("birth", "01.02.3999")
                )
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateUser_shouldUpdateUser() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(mockUser))
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(BASE_URL + port + "/user/1/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("name", "new-user")
                        .with("surname", "new-surname")
                        .with("email", "test02@mail.ru")
                        .with("birth", "20.06.1900")
                )
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void changePassword_shouldChangePassword() {
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(mockUser))
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(BASE_URL + port + "/user/1/change-password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("password", "password")
                )
                .exchange()
                .expectStatus().is3xxRedirection();
    }
}
