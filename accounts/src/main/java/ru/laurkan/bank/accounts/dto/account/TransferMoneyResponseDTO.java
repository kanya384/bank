package ru.laurkan.bank.accounts.dto.account;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransferMoneyResponseDTO {
    private boolean completed;
}
