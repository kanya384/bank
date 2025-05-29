package ru.laurkan.bank.exchangegen.dto;

import lombok.Getter;
import lombok.Setter;
import ru.laurkan.bank.exchangegen.model.ExchangeRate;

import java.util.List;

@Getter
@Setter
public class UpdateExchangeRateRequest {
    List<ExchangeRate> rates;
}
