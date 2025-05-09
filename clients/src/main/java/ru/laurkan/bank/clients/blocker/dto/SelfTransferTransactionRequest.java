package ru.laurkan.bank.clients.blocker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SelfTransferTransactionRequest {
    @NotNull
    private Long accountId;
    @NotNull
    private Long receiverAccountId;
    @NotNull
    @Positive
    private Double amount;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
}
