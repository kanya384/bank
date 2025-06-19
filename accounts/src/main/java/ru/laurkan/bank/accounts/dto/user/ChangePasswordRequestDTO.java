package ru.laurkan.bank.accounts.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ChangePasswordRequestDTO {
    @Size(min = 8)
    private String password;
}
