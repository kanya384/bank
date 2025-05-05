package ru.laurkan.bank.front.dto.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequestDTO {
    private String login;
    private String password;
}
