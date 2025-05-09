package ru.laurkan.bank.notifications.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.notifications.client.email.EmailClient;
import ru.laurkan.bank.notifications.dto.CreateEmailNotificationRequestDTO;
import ru.laurkan.bank.notifications.dto.EmailNotificationResponseDTO;
import ru.laurkan.bank.notifications.mapper.NotificationMapper;
import ru.laurkan.bank.notifications.model.EmailNotification;
import ru.laurkan.bank.notifications.repository.EmailNotificationRepository;
import ru.laurkan.bank.notifications.service.EmailNotificationService;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {
    private final EmailNotificationRepository emailNotificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailClient emailClient;

    @Override
    public Mono<EmailNotificationResponseDTO> create(CreateEmailNotificationRequestDTO request) {
        return Mono.just(request)
                .map(notificationMapper::map)
                .flatMap(emailNotificationRepository::save)
                .map(notificationMapper::map);
    }


    @Scheduled(fixedDelay = 3000)
    @Override
    public Flux<EmailNotification> sendMessages() {
        return emailNotificationRepository.findBySent(false)
                .flatMap(emailClient::sendMessage)
                .flatMap(message -> {
                    message.setSent(true);
                    return emailNotificationRepository.save(message);
                });
    }
}
