package ru.laurkan.bank.notifications.service;

import reactor.core.publisher.Mono;
import ru.laurkan.bank.events.accounts.AccountDetailedEvent;
import ru.laurkan.bank.events.cash.CashEventDetailed;
import ru.laurkan.bank.events.transfer.TransferEventDetailed;
import ru.laurkan.bank.events.users.UserDetailedEvent;
import ru.laurkan.bank.notifications.dto.EmailNotificationResponseDTO;

public interface EmailNotificationService {
    Mono<EmailNotificationResponseDTO> create(AccountDetailedEvent event);

    Mono<EmailNotificationResponseDTO> create(TransferEventDetailed event);

    Mono<EmailNotificationResponseDTO> create(CashEventDetailed event);

    Mono<EmailNotificationResponseDTO> create(UserDetailedEvent event);
}
