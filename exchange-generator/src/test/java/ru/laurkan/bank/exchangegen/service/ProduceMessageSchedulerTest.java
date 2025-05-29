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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@WebFluxTest({ProduceMessageScheduler.class,})
@ActiveProfiles("test")
public class ProduceMessageSchedulerTest {
    @MockitoBean
    private ExchangeClient exchangeClient;
    @Autowired
    private ProduceMessageScheduler produceMessageScheduler;

    @Test
    public void updateRates_shouldUpdateRatesInExchangeService() {
        when(exchangeClient.update(anyList()))
                .thenReturn(Flux.just(new ExchangeRateResponse()));

        StepVerifier.create(produceMessageScheduler.updateRates())
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }
}
