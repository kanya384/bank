package ru.laurkan.bank.clients.accounts.dto.accounts;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsersAccountsResponse {
    private Long id;
    private String surname;
    private String name;
    private List<AccountResponse> accounts;
}
