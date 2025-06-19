package ru.laurkan.bank.exchangegen.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class ExchangeRate {
    private Currency currency;
    private Double rate;
}
