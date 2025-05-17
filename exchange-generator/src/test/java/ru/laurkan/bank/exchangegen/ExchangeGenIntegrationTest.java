package ru.laurkan.bank.exchangegen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import ru.laurkan.bank.exchangegen.service.ExchangeService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(
        ids = "ru.laurkan:exchange:+:stubs:9060",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ExchangeGenIntegrationTest {
    private final String BASE_URL = "http://localhost:";

    @LocalServerPort
    int port;

    @Autowired
    private ExchangeService exchangeService;

    @Test
    void updateRates_shouldUpdateRatesInExchangeService() {
        StepVerifier.create(exchangeService.updateRates().collectList())
                .assertNext(response -> assertEquals(3, response.size()))
                .verifyComplete();
    }
}
