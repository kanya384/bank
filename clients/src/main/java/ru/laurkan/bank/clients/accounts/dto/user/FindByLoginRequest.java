package ru.laurkan.bank.clients.accounts.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FindByLoginRequest {
    private String login;
}
