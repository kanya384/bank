package ru.laurkan.bank.exchangegen.dto;

import lombok.Getter;
import lombok.Setter;
import ru.laurkan.bank.exchangegen.model.Currency;

@Getter
@Setter
public class ExchangeRateResponseDTO {
    private Currency currency;
    private Double rate;
}
