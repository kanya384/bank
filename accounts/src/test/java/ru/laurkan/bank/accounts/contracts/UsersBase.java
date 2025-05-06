package ru.laurkan.bank.accounts.contracts;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import ru.laurkan.bank.accounts.AbstractTestContainer;
import ru.laurkan.bank.accounts.model.User;
import ru.laurkan.bank.accounts.repository.AccountRepository;
import ru.laurkan.bank.accounts.repository.UserRepository;

import java.time.LocalDate;

@AutoConfigureWebTestClient
@DirtiesContext
public abstract class UsersBase extends AbstractTestContainer {
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
                            .name("user")
                            .surname("surname")
                            .login("user")
                            .email("test01@mail.ru")
                            .password("$2a$10$NTOUe3xPczFdJMJ47Kdiie5ijUcN/7qZLh3RCXsy9tkDM4DsLA2xa")
                            .birth(LocalDate.of(1990, 6, 20))
                            .build())
                    .block();
        }
    }

}
