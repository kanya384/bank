package ru.laurkan.bank.accounts.dto.account;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutMoneyToAccountDTO {
    @Positive
    private Double amount;
}
