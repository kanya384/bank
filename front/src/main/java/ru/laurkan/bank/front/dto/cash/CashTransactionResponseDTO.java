package ru.laurkan.bank.front.dto.cash;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CashTransactionResponseDTO {
    private Long id;
    private Long accountId;
    private Double amount;
    private String transactionType;
    private String transactionStatus;
    private LocalDateTime createdAt;
}
