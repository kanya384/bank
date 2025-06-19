package ru.laurkan.bank.cash.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CreateTransactionRequestDTO {
    @NotNull
    private Long accountId;
    @NotNull
    private Double amount;
}
