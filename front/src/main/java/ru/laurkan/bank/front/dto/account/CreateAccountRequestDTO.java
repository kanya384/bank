package ru.laurkan.bank.front.dto.account;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountRequestDTO {
    @NotNull
    private Long userId;
    @NotNull
    private String currency;
}
