package ru.laurkan.bank.clients.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.ClientBase;
import ru.laurkan.bank.clients.notification.dto.CreateEmailNotificationRequest;
import ru.laurkan.bank.clients.notification.dto.EmailNotificationResponse;

@RequiredArgsConstructor
public class NotificationClient extends ClientBase {
    private final String baseUrl;
    private final WebClient webClient;

    public Mono<EmailNotificationResponse> sendEmailNotification(CreateEmailNotificationRequest request) {
        return Mono.just(webClient.post()
                        .uri(baseUrl + "/notification/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, EmailNotificationResponse.class));
    }
}
