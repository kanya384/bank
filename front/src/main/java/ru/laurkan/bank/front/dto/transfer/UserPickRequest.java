package ru.laurkan.bank.front.dto.transfer;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPickRequest {
    @NotNull
    private Long user;
}
