package ru.laurkan.bank.notifications.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import ru.laurkan.bank.events.users.UserDetailedEvent;
import ru.laurkan.bank.notifications.service.EmailNotificationService;

import static ru.laurkan.bank.notifications.configuration.KafkaConfiguration.USER_INPUT_EVENTS_TOPIC;

@Log4j2
@Component
@RequiredArgsConstructor
public class UsersConsumer {
    private final EmailNotificationService emailNotificationService;

    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 3000),
            dltTopicSuffix = "-dlt",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = USER_INPUT_EVENTS_TOPIC)
    public void processAccountEvent(UserDetailedEvent userDetailedEvent) {
        System.out.println("User event type: " + userDetailedEvent.userEvent().eventType());
        System.out.println("User created id: " + userDetailedEvent.userEvent().userId());
        System.out.println("User login = " + userDetailedEvent.userInfo().login());
        System.out.println("User event type email = " + userDetailedEvent.userInfo().email());
    }
}
