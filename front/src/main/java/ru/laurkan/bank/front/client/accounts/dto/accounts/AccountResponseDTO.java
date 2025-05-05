package ru.laurkan.bank.front.client.accounts.dto.accounts;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AccountResponseDTO {
    private Long id;
    private Long userId;
    private String currency;
    private Double amount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
