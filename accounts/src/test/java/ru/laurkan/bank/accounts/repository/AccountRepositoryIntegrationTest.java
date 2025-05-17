package ru.laurkan.bank.accounts.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.laurkan.bank.accounts.AbstractTestContainer;
import ru.laurkan.bank.accounts.model.Account;
import ru.laurkan.bank.accounts.model.Currency;
import ru.laurkan.bank.accounts.model.User;

import java.time.LocalDate;

@DirtiesContext
public class AccountRepositoryIntegrationTest extends AbstractTestContainer {
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
    public void findByUserId_shouldReturnAccountByUserId() {
        accountRepository.findByUserId(1L)
                .collectList()
                .doOnNext(account -> Assertions.assertThat(account)
                        .withFailMessage("Пользователь не должен быть пустой")
                        .isNotNull()
                        .withFailMessage("Только один аккаунт должен быть найден")
                        .hasSize(1)
                        .first()
                        .withFailMessage("id найденого аккаунта должен равняться 1")
                        .extracting(Account::getId)
                        .isEqualTo(1L)
                )
                .block();
    }

    @Test
    public void createDuplicateCurrencyAccount_shouldThrowDuplicationException() {
        StepVerifier.create(accountRepository.save(Account.builder()
                        .userId(1L)
                        .amount(0.0)
                        .currency(Currency.RUB)
                        .build()))
                .expectErrorMatches(error -> error instanceof DuplicateKeyException)
                .verify();

    }

    @Test
    public void readById_shouldReturnNothingIfNotExists() {
        StepVerifier.create(accountRepository.findById(999L))
                .verifyComplete();
    }

}
