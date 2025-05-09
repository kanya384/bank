package ru.laurkan.bank.transfer.accounts.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FindByLoginRequestDTO {
    private String login;
}
