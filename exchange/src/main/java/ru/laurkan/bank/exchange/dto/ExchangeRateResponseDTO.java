package ru.laurkan.bank.exchange.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.laurkan.bank.exchange.model.Currency;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public class ExchangeRateResponseDTO {
    private Currency currency;
    private Double rate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
