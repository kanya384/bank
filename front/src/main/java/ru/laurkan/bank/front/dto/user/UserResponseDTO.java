package ru.laurkan.bank.front.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;

    private String login;

    private String password;

    private String surname;

    private String name;

    private String email;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate birth;
}
