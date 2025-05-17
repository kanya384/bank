package ru.laurkan.bank.exchange.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.laurkan.bank.exchange.AbstractTestContainer;
import ru.laurkan.bank.exchange.model.Currency;
import ru.laurkan.bank.exchange.model.ExchangeRate;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
public class ExchangeRepositoryIntegrationTest extends AbstractTestContainer {
    @Autowired
    private ExchangeRepository exchangeRepository;

    @BeforeEach
    void setUp() {
        var count = exchangeRepository.count().block();
        if (count != null && count == 0) {
            var exchange = ExchangeRate.builder()
                    .rate(0.01)
                    .currency(Currency.USD)
                    .build();

            exchangeRepository.save(exchange)
                    .block();
        }
    }

    @Test
    public void findByCurrency_shouldReturnExchangeByCurrency() {
        StepVerifier.create(exchangeRepository.findByCurrency(Currency.USD))
                .assertNext(exchangeRate -> assertThat(exchangeRate)
                        .isNotNull()
                        .extracting(ExchangeRate::getCurrency)
                        .isEqualTo(Currency.USD)
                )
                .verifyComplete();
    }

}
