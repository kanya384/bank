package ru.laurkan.bank.clients.cash.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTransactionRequest {
    @NotNull
    private Long accountId;
    @NotNull
    private Double amount;
}
