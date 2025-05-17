package ru.laurkan.bank.accounts.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.laurkan.bank.accounts.AbstractTestContainer;
import ru.laurkan.bank.accounts.dto.user.*;
import ru.laurkan.bank.accounts.model.Account;
import ru.laurkan.bank.accounts.model.Currency;
import ru.laurkan.bank.accounts.model.User;
import ru.laurkan.bank.accounts.repository.AccountRepository;
import ru.laurkan.bank.accounts.repository.UserRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext
public class UserControllerIntegrationTest extends AbstractTestContainer {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        if (userRepository.findById(1L).block() == null) {
            var user = User.builder()
                    .login("test")
                    .name("test")
                    .surname("test")
                    .email("test01@mail.ru")
                    .birth(LocalDate.of(1990, 6, 20))
                    .password("password")
                    .build();

            userRepository.save(user)
                    .block();
        }

        if (accountRepository.findById(1L).block() == null) {
            var account = Account.builder()
                    .userId(1L)
                    .amount(0.0)
                    .currency(Currency.RUB)
                    .build();

            accountRepository.save(account)
                    .block();
        }
    }

    @Test
    public void findByLogin_shouldFindUserByLogin() {
        var request = new FindByLoginRequestDTO();
        request.setLogin("test");

        webTestClient.post()
                .uri("/user/find-by-login", 1)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.login").isEqualTo("test");
    }

    @Test
    public void findByLogin_shouldReturn404IfNoUser() {
        var request = new FindByLoginRequestDTO();
        request.setLogin("test-not-existing");

        webTestClient.post()
                .uri("/user/find-by-login", 1)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void findAllUsersWithAccounts_shouldReturnOneUserWithAccounts() {
        var userWithoutAccounts = User.builder()
                .login("test-1")
                .name("test")
                .surname("test")
                .email("test02@mail.ru")
                .birth(LocalDate.of(1990, 6, 20))
                .password("password")
                .build();

        userRepository.save(userWithoutAccounts)
                .block();

        webTestClient.get()
                .uri("/user/all-with-accounts")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(User.class)
                .hasSize(1);
    }

    @Test
    public void registerUser_shouldRegisterUser() {
        var request = RegisterUserRequestDTO.builder()
                .login("test-new-not-registered")
                .name("test")
                .surname("test")
                .email("test99@yandex.ru")
                .birth(LocalDate.of(1990, 6, 20))
                .password("password")
                .build();

        webTestClient.post()
                .uri("/user/register")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponseDTO.class);
    }

    @Test
    public void updateUser_shouldUpdateUser() {
        var request = UpdateUserRequestDTO.builder()
                .name("test-new")
                .surname("test-new")
                .email("test888@mail.ru")
                .birth(LocalDate.of(1990, 6, 20))
                .build();

        webTestClient.put()
                .uri("/user/{id}", 1)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponseDTO.class);

        userRepository.findById(1L)
                .doOnNext(user -> {
                    assertEquals(request.getName(), user.getName());
                    assertEquals(request.getSurname(), user.getSurname());
                    assertEquals(request.getEmail(), user.getEmail());
                    assertEquals(request.getBirth(), user.getBirth());
                })
                .block();
    }

    @Test
    public void changePassword_shouldChangePassword() {
        var request = new ChangePasswordRequestDTO();
        request.setPassword("new-password");

        webTestClient.put()
                .uri("/user/change-password/{id}", 1)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponseDTO.class);

        userRepository.findById(1L)
                .doOnNext(user -> {
                    assertTrue(passwordEncoder.matches(request.getPassword(), user.getPassword()));
                })
                .block();
    }

}
