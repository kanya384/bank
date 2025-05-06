package ru.laurkan.bank.notifications.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateEmailNotificationRequestDTO {
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
