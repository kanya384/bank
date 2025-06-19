package ru.laurkan.bank.exchangegen.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.laurkan.bank.events.exchange.UpdateExchangeRateEvent;

@Configuration
public class KafkaConfiguration {
    @Bean
    public KafkaTemplate<String, UpdateExchangeRateEvent> kafkaTemplate(ProducerFactory<String, UpdateExchangeRateEvent> producerFactory) {
        KafkaTemplate<String, UpdateExchangeRateEvent> template = new KafkaTemplate<>(producerFactory);
        template.setObservationEnabled(true);
        return template;
    }
}
