package ru.laurkan.bank.notifications.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.notifications.dto.CreateEmailNotificationRequestDTO;
import ru.laurkan.bank.notifications.dto.EmailNotificationResponseDTO;
import ru.laurkan.bank.notifications.model.EmailNotification;

public interface EmailNotificationService {
    Mono<EmailNotificationResponseDTO> create(CreateEmailNotificationRequestDTO request);

    Flux<EmailNotification> sendMessages();
}
