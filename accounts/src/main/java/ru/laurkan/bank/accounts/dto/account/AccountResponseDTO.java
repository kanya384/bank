package ru.laurkan.bank.accounts.dto.account;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime modifiedAt;
}
