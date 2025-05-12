package ru.laurkan.bank.front.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.dto.account.AccountResponseDTO;
import ru.laurkan.bank.front.dto.account.CreateAccountRequestDTO;

import java.util.List;

public interface AccountsService {
    Flux<AccountResponseDTO> readAccountsOfUser();

    Flux<AccountResponseDTO> readAccountsOfUserById(Long userId);

    Mono<Void> deleteAccount(Long accountId);

    Mono<AccountResponseDTO> createAccount(CreateAccountRequestDTO request);

    Mono<List<String>> notExistingAccountCurrencyListOfUser();
}
