package ru.laurkan.bank.accounts.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsersAccountsResponseDTO {
    private Long id;
    private String surname;
    private String name;
}
