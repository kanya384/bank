package ru.laurkan.bank.events.exchange;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExchangeRateEventItem {
    private String currency;
    private Double rate;
}
