package ru.laurkan.bank.notifications.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.events.accounts.AccountDetailedEvent;
import ru.laurkan.bank.events.cash.CashEventDetailed;
import ru.laurkan.bank.events.transfer.TransferEventDetailed;
import ru.laurkan.bank.events.users.UserDetailedEvent;
import ru.laurkan.bank.notifications.dto.EmailNotificationResponseDTO;
import ru.laurkan.bank.notifications.mapper.NotificationMapper;
import ru.laurkan.bank.notifications.repository.EmailNotificationRepository;
import ru.laurkan.bank.notifications.service.EmailNotificationService;
import ru.laurkan.bank.notifications.utils.MessageBuilders;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {
    private final EmailNotificationRepository emailNotificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Mono<EmailNotificationResponseDTO> create(AccountDetailedEvent event) {
        return Mono.just(MessageBuilders.buildMessage(event))
                .flatMap(emailNotificationRepository::save)
                .map(notificationMapper::map);
    }

    @Override
    public Mono<EmailNotificationResponseDTO> create(TransferEventDetailed event) {
        return Mono.just(MessageBuilders.buildMessage(event))
                .flatMap(emailNotificationRepository::save)
                .map(notificationMapper::map);
    }

    @Override
    public Mono<EmailNotificationResponseDTO> create(CashEventDetailed event) {
        return Mono.just(MessageBuilders.buildMessage(event))
                .flatMap(emailNotificationRepository::save)
                .map(notificationMapper::map);
    }

    @Override
    public Mono<EmailNotificationResponseDTO> create(UserDetailedEvent event) {
        return Mono.just(MessageBuilders.buildMessage(event))
                .flatMap(emailNotificationRepository::save)
                .map(notificationMapper::map);
    }
}
