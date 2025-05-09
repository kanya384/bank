package ru.laurkan.bank.transfer.accounts.dto.accounts;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateAccountRequestDTO {
    @NotNull
    private Long userId;
    @NotNull
    private String currency;
}