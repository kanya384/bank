package ru.laurkan.bank.exchangegen.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExchangeRate {
    private Currency currency;
    private Double rate;
}
