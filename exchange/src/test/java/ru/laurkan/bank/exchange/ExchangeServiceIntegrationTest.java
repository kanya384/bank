package ru.laurkan.bank.exchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.laurkan.bank.exchange.dto.ExchangeRateResponseDTO;
import ru.laurkan.bank.exchange.model.Currency;
import ru.laurkan.bank.exchange.model.ExchangeRate;
import ru.laurkan.bank.exchange.repository.ExchangeRepository;
import ru.laurkan.bank.exchange.service.ExchangeService;

@DirtiesContext
public class ExchangeServiceIntegrationTest extends AbstractTestContainer {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Autowired
    private ExchangeService exchangeService;

    @BeforeEach
    void setUp() {
        var count = exchangeRepository.count()
                .block();
        if (count != null && count == 0) {
            var rub = ExchangeRate.builder()
                    .currency(Currency.RUB)
                    .rate(1.0)
                    .build();

            var usd = ExchangeRate.builder()
                    .currency(Currency.USD)
                    .rate(0.011)
                    .build();

            var cny = ExchangeRate.builder()
                    .currency(Currency.CNY)
                    .rate(0.04)
                    .build();

            exchangeRepository.save(rub)
                    .block();

            exchangeRepository.save(usd)
                    .block();

            exchangeRepository.save(cny)
                    .block();
        }
    }

    @Test
    public void read_shouldReturnExchangeRates() {
        webTestClient.get()
                .uri("/exchange-rates")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ExchangeRateResponseDTO.class)
                .hasSize(3);
    }

    @Test
    public void readByCurrency_shouldReturnExchangeRateByCurrency() {
        webTestClient.get()
                .uri("/exchange-rates/USD")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.currency")
                .isEqualTo("USD");
    }
}
