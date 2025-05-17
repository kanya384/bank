package ru.laurkan.bank.notifications;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.laurkan.bank.notifications.dto.CreateEmailNotificationRequestDTO;
import ru.laurkan.bank.notifications.dto.EmailNotificationResponseDTO;
import ru.laurkan.bank.notifications.repository.EmailNotificationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext
public class EmailNotificationControllerIntegrationTest extends AbstractTestContainer {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private EmailNotificationRepository emailNotificationRepository;

    @Test
    public void save_shouldSaveEmailNotification() {
        var request = new CreateEmailNotificationRequestDTO();
        request.setSubject("subject");
        request.setMessage("message");
        request.setRecipient("test01@mail.ru");

        webTestClient.post()
                .uri("/notification/email")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBodyList(EmailNotificationResponseDTO.class)
                .hasSize(1);

        var count = emailNotificationRepository.count()
                .block();

        assertEquals(1, count);
    }
}
