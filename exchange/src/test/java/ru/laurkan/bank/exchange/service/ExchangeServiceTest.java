package ru.laurkan.bank.exchange.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.laurkan.bank.exchange.dto.ExchangeRateResponseDTO;
import ru.laurkan.bank.exchange.mapper.ExchangeMapperImpl;
import ru.laurkan.bank.exchange.model.Currency;
import ru.laurkan.bank.exchange.model.ExchangeRate;
import ru.laurkan.bank.exchange.repository.ExchangeRepository;
import ru.laurkan.bank.exchange.service.impl.ExchangeServiceImpl;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@WebFluxTest({ExchangeServiceImpl.class, ExchangeMapperImpl.class})
@ActiveProfiles("test")
public class ExchangeServiceTest {
    @MockitoBean
    private ExchangeRepository exchangeRepository;

    @Autowired
    private ExchangeService exchangeService;

    @BeforeEach
    public void setUp() {
        reset(exchangeRepository);
    }

    @Test
    public void readExchangeRates_shouldReturnExchangeRates() {
        when(exchangeRepository.findAll())
                .thenReturn(Flux.just(ExchangeRate.builder()
                        .id(1L)
                        .currency(Currency.RUB)
                        .rate(1.0)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build()));

        StepVerifier.create(exchangeService.readExchangeRates()
                        .collectList())
                .assertNext(exchangeRateResponseDTOS -> assertThat(exchangeRateResponseDTOS)
                        .hasSize(1)
                        .first()
                        .extracting(ExchangeRateResponseDTO::getCurrency)
                        .isEqualTo(Currency.RUB)
                ).verifyComplete();
    }


    @Test
    public void readExchangeRateByCurrency_shouldReturnExchangeRateByCurrency() {
        when(exchangeRepository.findByCurrency(Currency.RUB))
                .thenReturn(Mono.just(ExchangeRate.builder()
                        .id(1L)
                        .currency(Currency.RUB)
                        .rate(1.0)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build()));

        StepVerifier.create(exchangeService.readExchangeRateByCurrency(Currency.RUB))
                .assertNext(exchangeRateResponseDTO -> assertThat(exchangeRateResponseDTO)
                        .extracting(ExchangeRateResponseDTO::getCurrency)
                        .isEqualTo(Currency.RUB)
                )
                .verifyComplete();
    }
}
