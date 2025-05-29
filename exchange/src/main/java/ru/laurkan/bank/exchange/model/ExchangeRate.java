package ru.laurkan.bank.exchange.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Table("exchange_rates")
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {
    @Id
    private Long id;
    private Currency currency;
    private Double rate;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
