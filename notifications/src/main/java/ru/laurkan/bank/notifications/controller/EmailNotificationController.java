package ru.laurkan.bank.notifications.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.notifications.dto.CreateEmailNotificationRequestDTO;
import ru.laurkan.bank.notifications.dto.EmailNotificationResponseDTO;
import ru.laurkan.bank.notifications.service.EmailNotificationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class EmailNotificationController {
    private final EmailNotificationService emailNotificationService;

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EmailNotificationResponseDTO> save(@RequestBody @Valid CreateEmailNotificationRequestDTO request) {
        return emailNotificationService.create(request);
    }
}
