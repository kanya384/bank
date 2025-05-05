package ru.laurkan.bank.front.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.client.accounts.dto.accounts.AccountResponseDTO;
import ru.laurkan.bank.front.client.accounts.dto.accounts.CreateAccountRequestDTO;

import java.util.List;

public interface AccountsService {
    Flux<AccountResponseDTO> readAccountsOfUser();

    Mono<Void> deleteAccount(Long accountId);

    Mono<AccountResponseDTO> createAccount(CreateAccountRequestDTO request);

    Mono<List<String>> notExistingAccountCurrencyListOfUser();
}
