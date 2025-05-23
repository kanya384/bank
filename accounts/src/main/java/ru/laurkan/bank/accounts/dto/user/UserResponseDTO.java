package ru.laurkan.bank.accounts.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;

    private String login;

    private String surname;

    private String name;

    private String password;

    private String email;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birth;
}
