package ru.laurkan.bank.clients.exchange.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateExchangeRateRequest {
    @NotNull
    private Currency currency;
    @NotNull
    @Positive
    private Double rate;
}
