package ru.laurkan.bank.cash.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;

@Configuration
public class KafkaConfiguration {
    public static final String OUTPUT_CASH_NOTIFICATION_EVENTS_TOPIC = "cash-notification-events";

    @Bean
    public NewTopics topics() {
        return new NewTopics(
                TopicBuilder.name(OUTPUT_CASH_NOTIFICATION_EVENTS_TOPIC)
                        .build()
        );
    }
}
