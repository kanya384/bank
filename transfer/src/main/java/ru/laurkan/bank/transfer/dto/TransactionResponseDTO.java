package ru.laurkan.bank.transfer.dto;

import lombok.Getter;
import lombok.Setter;
import ru.laurkan.bank.transfer.model.TransactionStatus;
import ru.laurkan.bank.transfer.model.TransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponseDTO {
    private Long id;
    private Long accountId;
    private Long receiverAccountId;
    private Double amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private LocalDateTime createdAt;
}
