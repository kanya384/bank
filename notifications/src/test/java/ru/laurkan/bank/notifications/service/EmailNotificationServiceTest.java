package ru.laurkan.bank.notifications.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.laurkan.bank.notifications.dto.CreateEmailNotificationRequestDTO;
import ru.laurkan.bank.notifications.mapper.NotificationMapperImpl;
import ru.laurkan.bank.notifications.model.EmailNotification;
import ru.laurkan.bank.notifications.repository.EmailNotificationRepository;
import ru.laurkan.bank.notifications.service.impl.EmailNotificationServiceImpl;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@WebFluxTest({EmailNotificationServiceImpl.class, NotificationMapperImpl.class})
@ActiveProfiles("test")
public class EmailNotificationServiceTest {
    @MockitoBean
    private EmailNotificationRepository emailNotificationRepository;

    @Autowired
    private EmailNotificationService emailNotificationService;

    @BeforeEach
    public void setUp() {
        reset(emailNotificationRepository);
    }

    @Test
    public void create_shouldCreateEmailNotification() {
        var request = new CreateEmailNotificationRequestDTO();
        request.setSubject("subject");
        request.setMessage("message");
        request.setRecipient("test01@mail.ru");

        when(emailNotificationRepository.save(any(EmailNotification.class)))
                .thenReturn(Mono.just(EmailNotification.builder()
                        .id(1L)
                        .subject("subject")
                        .message("message")
                        .recipient("test01@mail.ru")
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .sent(false)
                        .build()));

        StepVerifier.create(emailNotificationService.create(request))
                .assertNext(emailNotificationResponseDTO ->
                        assertThat(emailNotificationResponseDTO)
                                .isNotNull())
                .verifyComplete();
    }
}
