package ru.laurkan.bank.exchange.contracts;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.laurkan.bank.exchange.AbstractTestContainer;
import ru.laurkan.bank.exchange.SecurityTestConfiguration;
import ru.laurkan.bank.exchange.model.Currency;
import ru.laurkan.bank.exchange.model.ExchangeRate;
import ru.laurkan.bank.exchange.repository.ExchangeRepository;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@DirtiesContext
@ActiveProfiles("test")
@Import(SecurityTestConfiguration.class)
public class ContractVerifierBase extends AbstractTestContainer {
    @LocalServerPort
    int port;

    @Autowired
    private ExchangeRepository exchangeRepository;

    @BeforeEach
    public void beforeEach() {
        RestAssured.baseURI = "http://localhost:" + this.port;

        var count = exchangeRepository.count().block();
        if (count != null && count == 0) {
            exchangeRepository.save(ExchangeRate.builder()
                    .rate(1.0)
                    .currency(Currency.RUB)
                    .build()).block();

            exchangeRepository.save(ExchangeRate.builder()
                    .rate(0.01)
                    .currency(Currency.USD)
                    .build()).block();

            exchangeRepository.save(ExchangeRate.builder()
                    .rate(0.03)
                    .currency(Currency.CNY)
                    .build()).block();
        }
    }
}
