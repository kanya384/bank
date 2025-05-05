package ru.laurkan.bank.accounts.dto.account;

import lombok.Getter;
import lombok.Setter;
import ru.laurkan.bank.accounts.model.Currency;

import java.time.LocalDateTime;

@Getter
@Setter
public class AccountResponseDTO {
    private Long id;
    private Long userId;
    private Currency currency;
    private Double amount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
