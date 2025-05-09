package ru.laurkan.bank.clients.exchange.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExchangeRateResponse {
    private Currency currency;
    private Double rate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
