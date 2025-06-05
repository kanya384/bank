package ru.laurkan.bank.notifications.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfiguration {
    public static final String ACCOUNT_INPUT_EVENTS_TOPIC = "account-detailed-events";
    public static final String USER_INPUT_EVENTS_TOPIC = "user-detailed-events";
    public static final String CASH_INPUT_EVENTS_TOPIC = "cash-detailed-events";
    public static final String TRANSFER_INPUT_EVENTS_TOPIC = "transfer-detailed-events";


}
