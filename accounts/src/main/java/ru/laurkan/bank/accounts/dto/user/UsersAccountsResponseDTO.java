package ru.laurkan.bank.accounts.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.laurkan.bank.accounts.dto.account.AccountResponseDTO;

import java.util.List;

@Getter
@Setter
@Builder
public class UsersAccountsResponseDTO {
    private Long id;
    private String surname;
    private String name;
    private List<AccountResponseDTO> accounts;
}
