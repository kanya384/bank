package ru.laurkan.bank.events.exchange;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateExchangeRateEvent {
    List<ExchangeRateEventItem> rates;
}
