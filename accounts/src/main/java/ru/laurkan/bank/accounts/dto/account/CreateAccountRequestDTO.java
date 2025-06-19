package ru.laurkan.bank.accounts.dto.account;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.laurkan.bank.accounts.validators.PresentedCurrency;

@Getter
@Setter
@Data
public class CreateAccountRequestDTO {
    @NotNull
    private Long userId;
    @PresentedCurrency
    private String currency;
}