package ru.laurkan.bank.notifications.service;

import reactor.core.publisher.Mono;
import ru.laurkan.bank.notifications.dto.CreateEmailNotificationRequestDTO;
import ru.laurkan.bank.notifications.dto.EmailNotificationResponseDTO;

public interface EmailNotificationService {
    Mono<EmailNotificationResponseDTO> create(CreateEmailNotificationRequestDTO request);
}
