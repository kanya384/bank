package ru.laurkan.bank.accounts.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.laurkan.bank.events.accounts.AccountEvent;
import ru.laurkan.bank.events.accounts.AccountInfo;
import ru.laurkan.bank.events.users.UserInfo;

@Configuration
public class KafkaConfiguration {
    public static final String OUTPUT_ACCOUNT_EVENTS_TOPIC = "account-events";
    public static final String OUTPUT_USER_EVENTS_TOPIC = "user-events";
    public static final String ACCOUNT_NOTIFICATION_EVENTS_TOPIC = "account-notification-events";
    public static final String USER_NOTIFICATION_EVENTS_TOPIC = "user-notification-events";

    @Bean
    public NewTopics topics() {
        return new NewTopics(
                TopicBuilder.name(OUTPUT_ACCOUNT_EVENTS_TOPIC)
                        .build(),
                TopicBuilder.name(OUTPUT_USER_EVENTS_TOPIC)
                        .build(),
                TopicBuilder.name(ACCOUNT_NOTIFICATION_EVENTS_TOPIC)
                        .build(),
                TopicBuilder.name(USER_NOTIFICATION_EVENTS_TOPIC)
                        .build()
        );
    }

    @Bean
    public KafkaTemplate<Long, AccountInfo> domainAccountEvents(ProducerFactory<Long, AccountInfo> producerFactory) {
        KafkaTemplate<Long, AccountInfo> template = new KafkaTemplate<>(producerFactory);
        template.setObservationEnabled(true);
        return template;
    }

    @Bean
    public KafkaTemplate<Long, UserInfo> domainUserEvents(ProducerFactory<Long, UserInfo> producerFactory) {
        KafkaTemplate<Long, UserInfo> template = new KafkaTemplate<>(producerFactory);
        template.setObservationEnabled(true);
        return template;
    }

    @Bean
    public KafkaTemplate<Long, AccountEvent> notificationsTemplate(ProducerFactory<Long, AccountEvent> producerFactory) {
        KafkaTemplate<Long, AccountEvent> template = new KafkaTemplate<>(producerFactory);
        template.setObservationEnabled(true);
        return template;
    }
}
