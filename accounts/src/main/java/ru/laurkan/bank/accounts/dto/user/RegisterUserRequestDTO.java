package ru.laurkan.bank.accounts.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.laurkan.bank.accounts.validators.Adult;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequestDTO {
    @Size(min = 3)
    private String login;
    @Size(min = 8)
    private String password;
    @Size(min = 3)
    private String surname;
    @Size(min = 3)
    private String name;
    @Email
    private String email;
    @Adult
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birth;
}
