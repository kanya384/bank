package ru.laurkan.bank.blocker.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public abstract class Transaction {
    private Long accountId;
    private Double amount;
    private LocalDateTime createdAt;
}
