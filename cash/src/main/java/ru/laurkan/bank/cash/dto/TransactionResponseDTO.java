package ru.laurkan.bank.cash.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.laurkan.bank.cash.model.TransactionStatus;
import ru.laurkan.bank.cash.model.TransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class TransactionResponseDTO {
    private Long id;
    private Long accountId;
    private Double amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private LocalDateTime createdAt;
}
