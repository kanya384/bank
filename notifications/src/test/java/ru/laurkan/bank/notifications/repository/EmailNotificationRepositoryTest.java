package ru.laurkan.bank.notifications.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.laurkan.bank.notifications.AbstractTestContainer;
import ru.laurkan.bank.notifications.model.EmailNotification;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
public class EmailNotificationRepositoryTest extends AbstractTestContainer {
    @Autowired
    private EmailNotificationRepository emailNotificationRepository;

    @Test
    public void findBySent_shouldReturnNotificationsBySent() {
        var notification = new EmailNotification();
        notification.setSent(false);
        notification.setMessage("message");
        notification.setSubject("subject");
        notification.setRecipient("test01@mail.ru");

        emailNotificationRepository.save(notification)
                .block();

        notification.setSent(true);
        notification.setId(null);

        emailNotificationRepository.save(notification)
                .block();

        StepVerifier.create(emailNotificationRepository.findBySent(false).collectList())
                .assertNext(emailNotifications -> assertThat(emailNotifications)
                        .hasSize(1))
                .verifyComplete();
    }
}
