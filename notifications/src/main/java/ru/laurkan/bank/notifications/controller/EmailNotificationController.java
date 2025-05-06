package ru.laurkan.bank.notifications.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.notifications.client.email.EmailClient;
import ru.laurkan.bank.notifications.dto.CreateEmailNotificationRequestDTO;
import ru.laurkan.bank.notifications.dto.EmailNotificationResponseDTO;
import ru.laurkan.bank.notifications.model.EmailNotification;
import ru.laurkan.bank.notifications.service.EmailNotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class EmailNotificationController {
    private final EmailNotificationService emailNotificationService;
    private final EmailClient emailClient;

    @PostMapping("/email")
    public Mono<EmailNotificationResponseDTO> save(@RequestBody @Valid CreateEmailNotificationRequestDTO request) {
        return emailNotificationService.create(request);
    }

    @GetMapping("/email")
    public Mono<EmailNotification> send() {
        return null;
        //return emailClient.sendMessage("kanya384@yandex.ru", "test", "text content");
    }
}
