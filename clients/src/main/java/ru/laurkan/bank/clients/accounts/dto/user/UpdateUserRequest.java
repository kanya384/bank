package ru.laurkan.bank.clients.accounts.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserRequest {
    @Size(min = 3)
    private String surname;
    @Size(min = 3)
    private String name;
    @Email
    private String email;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birth;
}
