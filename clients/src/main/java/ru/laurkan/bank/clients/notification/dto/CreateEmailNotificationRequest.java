package ru.laurkan.bank.clients.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateEmailNotificationRequest {
    @Email
    @NotNull
    private String recipient;

    @NotNull
    @NotBlank
    private String subject;

    @NotNull
    @NotBlank
    private String message;
}
