package ru.laurkan.bank.front.dto.transfer;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponseDTO {
    private Long id;
    private Long accountId;
    private Long receiverAccountId;
    private Double amount;
    private String transactionType;
    private String transactionStatus;
    private LocalDateTime createdAt;
}
