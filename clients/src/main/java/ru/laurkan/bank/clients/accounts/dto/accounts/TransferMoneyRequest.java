package ru.laurkan.bank.clients.accounts.dto.accounts;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransferMoneyRequest {
    private Long fromAccountId;
    private Double fromMoneyAmount;
    private Long toAccountId;
    private Double toMoneyAmount;
}
