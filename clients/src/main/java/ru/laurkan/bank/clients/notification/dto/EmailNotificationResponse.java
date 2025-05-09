package ru.laurkan.bank.clients.notification.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmailNotificationResponse {
    private Long id;
    private String recipient;
    private String subject;
    private String message;
    private Boolean sent;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
