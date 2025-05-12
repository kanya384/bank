package ru.laurkan.bank.clients.transfer.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponse {
    private Long id;
    private Long accountId;
    private Long receiverAccountId;
    private Double amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private LocalDateTime createdAt;
}
