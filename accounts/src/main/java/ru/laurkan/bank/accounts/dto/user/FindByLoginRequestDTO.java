package ru.laurkan.bank.accounts.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindByLoginRequestDTO {
    private String login;
}
