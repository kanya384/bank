package ru.laurkan.bank.clients.accounts.dto.accounts;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TakeMoneyFromAccount {
    @Positive
    private Double amount;
}
