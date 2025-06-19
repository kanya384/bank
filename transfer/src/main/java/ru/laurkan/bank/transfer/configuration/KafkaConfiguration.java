package ru.laurkan.bank.transfer.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.laurkan.bank.events.transfer.TransferEvent;

@Configuration
public class KafkaConfiguration {
    public static final String OUTPUT_TRANSFER_NOTIFICATION_EVENTS_TOPIC = "transfer-notification-events";

    @Bean
    public NewTopics topics() {
        return new NewTopics(
                TopicBuilder.name(OUTPUT_TRANSFER_NOTIFICATION_EVENTS_TOPIC)
                        .build()
        );
    }

    @Bean
    public KafkaTemplate<Long, TransferEvent> notificationsTemplate(ProducerFactory<Long, TransferEvent> producerFactory) {
        KafkaTemplate<Long, TransferEvent> template = new KafkaTemplate<>(producerFactory);
        template.setObservationEnabled(true);
        return template;
    }
}
