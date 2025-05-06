package ru.laurkan.bank.blocker.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class Transaction {
    private Long accountId;
    private Double money;
    private LocalDateTime createdAt;
}
