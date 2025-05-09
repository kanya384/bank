package ru.laurkan.bank.exchange.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import ru.laurkan.bank.exchange.model.Currency;

@Getter
@Setter
public class UpdateExchangeRateRequestDTO {
    @NotNull
    private Currency currency;
    @NotNull
    @Positive
    private Double rate;
}
