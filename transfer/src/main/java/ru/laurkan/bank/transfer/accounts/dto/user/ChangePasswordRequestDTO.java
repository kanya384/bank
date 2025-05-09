package ru.laurkan.bank.transfer.accounts.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDTO {
    @Size(min = 8)
    private String password;
}
