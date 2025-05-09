package ru.laurkan.bank.accounts.dto.account;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferMoneyRequestDTO {
    private Long fromAccountId;
    private Double fromMoneyAmount;
    private Long toAccountId;
    private Double toMoneyAmount;
}
