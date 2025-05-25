package ru.laurkan.bank.front.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateUserRequestDTO {
    @Size(min = 3)
    private String surname;
    @Size(min = 3)
    private String name;
    @Email
    private String email;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate birth;
}
