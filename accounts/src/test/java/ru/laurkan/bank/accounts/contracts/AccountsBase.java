package ru.laurkan.bank.accounts.contracts;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import ru.laurkan.bank.accounts.AbstractTestContainer;
import ru.laurkan.bank.accounts.model.Account;
import ru.laurkan.bank.accounts.model.Currency;
import ru.laurkan.bank.accounts.model.User;
import ru.laurkan.bank.accounts.repository.AccountRepository;
import ru.laurkan.bank.accounts.repository.UserRepository;

import java.time.LocalDate;

@AutoConfigureWebTestClient
@DirtiesContext
public abstract class AccountsBase extends AbstractTestContainer {
    @LocalServerPort
    int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void beforeEach() {
        RestAssured.baseURI = "http://localhost:" + this.port;
        var count = userRepository.count()
                .block();

        if (count == 0) {
            userRepository.save(User.builder()
                            .name("user1")
                            .surname("surname1")
                            .login("user1")
                            .email("test01@mail.ru")
                            .password("password")
                            .birth(LocalDate.of(1990, 6, 20))
                            .build())
                    .block();

            userRepository.save(User.builder()
                            .name("user2")
                            .surname("surname2")
                            .login("user2")
                            .email("test02@mail.ru")
                            .password("password")
                            .birth(LocalDate.of(1990, 6, 20))
                            .build())
                    .block();
        }

        count = accountRepository.count().block();

        if (count == 0) {
            accountRepository.save(Account.builder()
                    .userId(1L)
                    .amount(0.0)
                    .currency(Currency.RUB)
                    .build()).block();

            accountRepository.save(Account.builder()
                    .userId(2L)
                    .amount(0.0)
                    .currency(Currency.RUB)
                    .build()).block();

            accountRepository.save(Account.builder()
                    .userId(2L)
                    .amount(0.0)
                    .currency(Currency.USD)
                    .build()).block();

            accountRepository.save(Account.builder()
                    .userId(2L)
                    .amount(0.0)
                    .currency(Currency.CNY)
                    .build()).block();
        }
    }

}
