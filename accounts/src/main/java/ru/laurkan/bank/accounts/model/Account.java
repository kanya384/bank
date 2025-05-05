package ru.laurkan.bank.accounts.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "accounts")
public class Account {
    @Id
    private Long id;
    private Long userId;
    private Currency currency;
    private Double amount;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public Account(Long userId, Currency currency) {
        this.amount = 0.0;
        this.userId = userId;
        this.currency = currency;
    }
}
