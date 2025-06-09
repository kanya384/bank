package ru.laurkan.bank.exchange.dto;

import lombok.Getter;
import lombok.Setter;
import ru.laurkan.bank.exchange.model.ExchangeRate;

import java.util.List;

@Getter
@Setter
public class UpdateExchangeRateRequest {
    List<ExchangeRate> rates;
}
