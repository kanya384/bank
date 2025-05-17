package ru.laurkan.bank.notifications.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "email_notification")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotification {
    @Id
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
