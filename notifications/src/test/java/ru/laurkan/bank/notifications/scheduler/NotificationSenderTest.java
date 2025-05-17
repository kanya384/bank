package ru.laurkan.bank.notifications.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.laurkan.bank.notifications.client.email.EmailClient;
import ru.laurkan.bank.notifications.mapper.NotificationMapperImpl;
import ru.laurkan.bank.notifications.model.EmailNotification;
import ru.laurkan.bank.notifications.repository.EmailNotificationRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@WebFluxTest({NotificationSender.class, NotificationMapperImpl.class})
@ActiveProfiles("test")
public class NotificationSenderTest {
    @MockitoBean
    private EmailNotificationRepository emailNotificationRepository;

    @MockitoBean
    private EmailClient emailClient;

    @Autowired
    private NotificationSender notificationSender;

    @BeforeEach
    public void setUp() {
        reset(emailNotificationRepository);
        reset(emailClient);
    }

    @Test
    public void create_shouldCreateEmailNotification() {
        var notification = EmailNotification.builder()
                .id(1L)
                .subject("subject")
                .message("message")
                .recipient("test01@mail.ru")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .sent(false)
                .build();

        when(emailNotificationRepository.findBySent(false))
                .thenReturn(Flux.just(notification));

        when(emailClient.sendMessage(any(EmailNotification.class)))
                .thenReturn(Mono.just(notification));

        when(emailNotificationRepository.save(any(EmailNotification.class)))
                .thenReturn(Mono.just(EmailNotification.builder()
                        .id(1L)
                        .subject("subject")
                        .message("message")
                        .recipient("test01@mail.ru")
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .sent(true)
                        .build()));


        StepVerifier.create(notificationSender.sendMessages()
                        .collectList())
                .assertNext(emailNotificationResponseDTOS ->
                        assertThat(emailNotificationResponseDTOS)
                                .hasSize(1))
                .verifyComplete();
    }
}
