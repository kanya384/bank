package ru.laurkan.bank.exchangegen.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.laurkan.bank.clients.exchange.ExchangeClient;
import ru.laurkan.bank.clients.exchange.dto.ExchangeRateResponse;
import ru.laurkan.bank.events.exchange.UpdateExchangeRateEvent;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@WebFluxTest({ProduceMessageScheduler.class,})
@ActiveProfiles("test")
public class ProduceMessageSchedulerTest {
    @MockitoBean
    private ExchangeClient exchangeClient;
    @Autowired
    private ProduceMessageScheduler produceMessageScheduler;

    @MockitoBean
    private KafkaTemplate<String, UpdateExchangeRateEvent> kafkaTemplate;

    @BeforeEach
    public void setUp() {
        reset(exchangeClient);
        reset(kafkaTemplate);
    }

    @Test
    public void updateRates_shouldUpdateRatesInExchangeService() {
        when(exchangeClient.update(anyList()))
                .thenReturn(Flux.just(new ExchangeRateResponse()));

        when(kafkaTemplate.send(any(String.class), any(String.class), any(UpdateExchangeRateEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(new SendResult<>(new ProducerRecord<>("test", 0,
                        "test",
                        new UpdateExchangeRateEvent()), new RecordMetadata(new TopicPartition("test", 0),
                        0L, 0, 0L, 0, 0))));

        StepVerifier.create(produceMessageScheduler.updateRates())
                .verifyComplete();
    }
}
