package ru.laurkan.bank.clients.transfer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTransactionRequestDTO {
    @NotNull
    private Long accountId;
    @NotNull
    private Long receiverAccountId;
    @NotNull
    private Double amount;
}
