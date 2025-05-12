package ru.laurkan.bank.front.dto.exchange;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExchangeRateResponseDTO {
    private String currency;
    private Double rate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
