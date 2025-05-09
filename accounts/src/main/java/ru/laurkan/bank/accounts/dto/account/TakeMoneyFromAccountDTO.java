package ru.laurkan.bank.accounts.dto.account;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TakeMoneyFromAccountDTO {
    @Positive
    private Double amount;
}
