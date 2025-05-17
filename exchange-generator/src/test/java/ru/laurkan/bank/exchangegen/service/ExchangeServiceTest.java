package ru.laurkan.bank.exchangegen.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.laurkan.bank.clients.exchange.ExchangeClient;
import ru.laurkan.bank.clients.exchange.dto.ExchangeRateResponse;
import ru.laurkan.bank.exchangegen.mapper.ExchangeMapperImpl;
import ru.laurkan.bank.exchangegen.service.impl.ExchangeServiceImpl;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@WebFluxTest({ExchangeServiceImpl.class, ExchangeMapperImpl.class})
@ActiveProfiles("test")
public class ExchangeServiceTest {
    @MockitoBean
    private ExchangeClient exchangeClient;
    @Autowired
    private ExchangeService exchangeService;

    @Test
    public void updateRates_shouldUpdateRatesInExchangeService() {
        when(exchangeClient.update(anyList()))
                .thenReturn(Flux.just(new ExchangeRateResponse()));

        StepVerifier.create(exchangeService.updateRates())
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }
}
