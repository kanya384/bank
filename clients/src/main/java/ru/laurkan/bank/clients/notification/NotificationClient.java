package ru.laurkan.bank.clients.notification;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.notification.dto.CreateEmailNotificationRequest;
import ru.laurkan.bank.clients.notification.dto.EmailNotificationResponse;

public class NotificationClient {
    private final String baseUrl;
    private final WebClient webClient;

    public NotificationClient(String baseUrl, WebClient webClient) {
        this.baseUrl = baseUrl;
        this.webClient = webClient;
    }

    public Mono<EmailNotificationResponse> sendEmailNotification(CreateEmailNotificationRequest request) {
        return webClient.post()
                .uri(baseUrl + "/notification/email")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmailNotificationResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }
}
