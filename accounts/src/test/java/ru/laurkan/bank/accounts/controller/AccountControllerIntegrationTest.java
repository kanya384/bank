package ru.laurkan.bank.accounts.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;
import ru.laurkan.bank.accounts.AbstractTestContainer;
import ru.laurkan.bank.accounts.dto.account.CreateAccountRequestDTO;
import ru.laurkan.bank.accounts.dto.account.PutMoneyToAccountDTO;
import ru.laurkan.bank.accounts.dto.account.TakeMoneyFromAccountDTO;
import ru.laurkan.bank.accounts.dto.account.TransferMoneyRequestDTO;
import ru.laurkan.bank.accounts.model.Account;
import ru.laurkan.bank.accounts.model.Currency;
import ru.laurkan.bank.accounts.model.User;
import ru.laurkan.bank.accounts.repository.AccountRepository;
import ru.laurkan.bank.accounts.repository.UserRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureWebTestClient
@DirtiesContext
public class AccountControllerIntegrationTest extends AbstractTestContainer {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

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
    public void readAccountById_shouldReturnAccountById() {
        webTestClient.get()
                .uri("/accounts/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    public void readAccountById_shouldReturn404() {
        webTestClient.get()
                .uri("/accounts/{id}", 99)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void readAccountsOfUser_shouldReturnAccountsOfUser() {
        webTestClient.get()
                .uri("/accounts/user/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Object.class)
                .hasSize(1);
    }

    @Test
    public void readAccountsOfUser_shouldReturnZero() {
        webTestClient.get()
                .uri("/accounts/user/{id}", 99)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Object.class)
                .hasSize(0);
    }

    @Test
    public void createAccount_shouldCreateAccountForUser() {
        var request = new CreateAccountRequestDTO();
        request.setUserId(1L);
        request.setCurrency(Currency.USD.toString());

        webTestClient.post()
                .uri("/accounts")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.userId").isEqualTo(1)
                .jsonPath("$.currency").isEqualTo("USD");
    }

    @Test
    public void deleteAccount_shouldDeleteAccountById() {
        var account = accountRepository.save(Account.builder()
                .userId(1L)
                .amount(0.0)
                .currency(Currency.CNY)
                .build()).block();

        assertNotNull(account);

        webTestClient.delete()
                .uri("/accounts/{id}", account.getId())
                .exchange()
                .expectStatus().isOk();

        StepVerifier.create(accountRepository.findById(account.getId()))
                .verifyComplete();
    }

    @Test
    public void deleteAccount_shouldReturnErrorIfAccountIsNotEmpty() {
        var account = accountRepository.save(Account.builder()
                        .userId(1L)
                        .amount(10.0)
                        .currency(Currency.CNY)
                        .build())
                .block();

        assertNotNull(account);

        webTestClient.delete()
                .uri("/accounts/{id}", account.getId())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("На аккаунте не должно быть денег");

        accountRepository.deleteById(account.getId())
                .block();
    }

    @Test
    public void putMoneyToAccount_shouldPutMoneyToAccount() {
        var account = accountRepository.save(Account.builder()
                .userId(1L)
                .amount(0.0)
                .currency(Currency.CNY)
                .build()).block();

        assertNotNull(account);

        var request = new PutMoneyToAccountDTO();
        request.setAmount(10.0);

        webTestClient.put()
                .uri("/accounts/{id}/put-money", account.getId())
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(10.0);

        accountRepository.deleteById(account.getId())
                .block();
    }

    @Test
    public void putMoneyToAccount_shouldReturnErrorIfNoAccount() {
        var request = new PutMoneyToAccountDTO();
        request.setAmount(10.0);

        webTestClient.put()
                .uri("/accounts/{id}/put-money", 99)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void takeMoneyFromAccount_shouldTakeMoneyFromAccount() {
        var account = accountRepository.save(Account.builder()
                .userId(1L)
                .amount(50.0)
                .currency(Currency.CNY)
                .build()).block();

        assertNotNull(account);

        var request = TakeMoneyFromAccountDTO.builder()
                .amount(10.0)
                .build();

        webTestClient.put()
                .uri("/accounts/{id}/take-money", account.getId())
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.amount").isEqualTo(40.0);

        accountRepository.deleteById(account.getId())
                .block();
    }

    @Test
    public void takeMoneyFromAccount_shouldReturnErrorIfNoAccount() {
        var request = TakeMoneyFromAccountDTO.builder()
                .amount(10.0)
                .build();

        webTestClient.put()
                .uri("/accounts/{id}/take-money", 99)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void takeMoneyFromAccount_shouldReturnErrorIfNotEnoughMoney() {
        var account = accountRepository.save(Account.builder()
                .userId(1L)
                .amount(5.0)
                .currency(Currency.CNY)
                .build()).block();

        assertNotNull(account);

        var request = TakeMoneyFromAccountDTO.builder()
                .amount(10.0)
                .build();

        webTestClient.put()
                .uri("/accounts/{id}/take-money", 1)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.PAYMENT_REQUIRED);
    }


    @Test
    public void transferMoney_shouldTransferMoney() {
        var user = userRepository.save(User.builder()
                .login("test-t")
                .name("test-t")
                .surname("test-t")
                .password("test-t")
                .email("tes@email.ru")
                .birth(LocalDate.now())
                .build()).block();
        
        assertNotNull(user);

        var account1 = accountRepository.save(Account.builder()
                .userId(user.getId())
                .amount(50.0)
                .currency(Currency.CNY)
                .build()).block();

        var account2 = accountRepository.save(Account.builder()
                .userId(user.getId())
                .amount(0.0)
                .currency(Currency.USD)
                .build()).block();

        assertNotNull(account1);
        assertNotNull(account2);

        var request = new TransferMoneyRequestDTO();
        request.setFromAccountId(account1.getId());
        request.setFromMoneyAmount(10.0);
        request.setToAccountId(account2.getId());
        request.setToMoneyAmount(5.0);

        webTestClient.put()
                .uri("/accounts/transfer-money")
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.completed").isEqualTo(true);

        accountRepository.findById(account1.getId())
                .doOnNext(account -> assertEquals(40.0, account.getAmount()))
                .block();

        accountRepository.findById(account2.getId())
                .doOnNext(account -> assertEquals(5.0, account.getAmount()))
                .block();

        userRepository.deleteById(user.getId())
                .block();
    }
}
