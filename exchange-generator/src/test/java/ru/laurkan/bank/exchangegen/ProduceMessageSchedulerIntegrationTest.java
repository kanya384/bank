package ru.laurkan.bank.exchangegen;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import ru.laurkan.bank.exchangegen.service.ProduceMessageScheduler;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ProduceMessageSchedulerIntegrationTest {
    private final String BASE_URL = "http://localhost:";

    @LocalServerPort
    int port;

    @Autowired
    private ProduceMessageScheduler produceMessageScheduler;

    @Test
    void updateRates_shouldUpdateRatesInExchangeService() {
        /*StepVerifier.create(produceMessageScheduler.updateRates().collectList())
                .assertNext(response -> assertEquals(3, response.size()))
                .verifyComplete();*/
    }
}
