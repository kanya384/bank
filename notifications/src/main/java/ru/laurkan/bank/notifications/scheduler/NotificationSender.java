package ru.laurkan.bank.notifications.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.laurkan.bank.notifications.client.email.EmailClient;
import ru.laurkan.bank.notifications.model.EmailNotification;
import ru.laurkan.bank.notifications.repository.EmailNotificationRepository;

@Component
@RequiredArgsConstructor
public class NotificationSender {
    private final EmailNotificationRepository emailNotificationRepository;
    private final EmailClient emailClient;

    @Scheduled(fixedDelay = 3000)
    public Flux<EmailNotification> sendMessages() {
        return emailNotificationRepository.findBySent(false)
                .flatMap(emailClient::sendMessage)
                .flatMap(message -> {
                    message.setSent(true);
                    return emailNotificationRepository.save(message);
                });
    }
}
