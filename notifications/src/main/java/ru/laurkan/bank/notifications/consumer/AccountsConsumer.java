package ru.laurkan.bank.notifications.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import ru.laurkan.bank.events.accounts.AccountDetailedEvent;
import ru.laurkan.bank.notifications.service.EmailNotificationService;

import static ru.laurkan.bank.notifications.configuration.KafkaConfiguration.ACCOUNT_INPUT_EVENTS_TOPIC;

@Component
@RequiredArgsConstructor
public class AccountsConsumer {
    private final EmailNotificationService emailNotificationService;

    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 8000),
            dltTopicSuffix = "-dlt",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = ACCOUNT_INPUT_EVENTS_TOPIC)
    public void processAccountEvent(AccountDetailedEvent accountDetailedEvent) {
        System.out.println("Account event type: " + accountDetailedEvent.accountEvent().eventType());
        System.out.println("Account event type email = " + accountDetailedEvent.userInfo().email());
    }
}
