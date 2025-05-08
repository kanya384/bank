package ru.laurkan.bank.clients.accounts.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @Size(min = 8)
    private String password;
}
