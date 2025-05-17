package ru.laurkan.bank.accounts.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.laurkan.bank.accounts.AbstractTestContainer;
import ru.laurkan.bank.accounts.model.Account;
import ru.laurkan.bank.accounts.model.Currency;
import ru.laurkan.bank.accounts.model.User;

import java.time.LocalDate;

@DirtiesContext
public class UserRepositoryIntegrationTest extends AbstractTestContainer {
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
    public void findByLogin_shouldReturnUserByLogin() {
        userRepository.findByLogin("test")
                .doOnNext(account -> Assertions.assertThat(account)
                        .withFailMessage("Пользователь не должен быть пустой")
                        .isNotNull()
                        .withFailMessage("id найденого пользователя должен равняться 1")
                        .extracting(User::getId)
                        .isEqualTo(1L)
                );
    }

    @Test
    public void findByAccountId_shouldReturnUserByAccountId() {
        userRepository.findByAccountId(1L)
                .doOnNext(account -> Assertions.assertThat(account)
                        .withFailMessage("Пользователь не должен быть пустой")
                        .isNotNull()
                        .withFailMessage("id найденого пользователя должен равняться 1")
                        .extracting(User::getId)
                        .isEqualTo(1L)
                );
    }

    @Test
    public void findAllUsersWithExistingAccounts_shouldReturnUserByAccountId() {
        StepVerifier.create(userRepository.findAllUsersWithExistingAccounts().collectList())
                .assertNext(users -> Assertions.assertThat(users)
                        .withFailMessage("Список пользователей не должен быть пустой")
                        .isNotNull()
                        .withFailMessage("Длина списка должна равняться 1")
                        .hasSize(1)
                        .first()
                        .extracting(User::getId)
                        .withFailMessage("Id пользователя должен равняться 1")
                        .isEqualTo(1L)
                )
                .verifyComplete();
    }

}
