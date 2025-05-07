package ru.laurkan.bank.notifications.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmailNotificationResponseDTO {
    private Long id;
    private String recipient;
    private String subject;
    private String message;
    private Boolean sent;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
