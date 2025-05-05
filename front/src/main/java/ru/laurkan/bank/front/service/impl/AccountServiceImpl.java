package ru.laurkan.bank.front.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.client.accounts.AccountsClient;
import ru.laurkan.bank.front.client.accounts.dto.accounts.AccountResponseDTO;
import ru.laurkan.bank.front.client.accounts.dto.accounts.CreateAccountRequestDTO;
import ru.laurkan.bank.front.service.AccountsService;
import ru.laurkan.bank.front.utils.SecurityUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountsService {
    private final AccountsClient accountsClient;

    private final List<String> defaultCurrencyList = List.of("USD", "RUB", "CNY");


    @Override
    public Flux<AccountResponseDTO> readAccountsOfUser() {
        return SecurityUtils.getUserId()
                .flatMapMany(accountsClient::readAccountsOfUser);
    }

    @Override
    public Mono<Void> deleteAccount(Long userId) {
        return accountsClient.deleteAccount(userId);
    }

    @Override
    @PreAuthorize("#request.userId == authentication.principal.id")
    public Mono<AccountResponseDTO> createAccount(CreateAccountRequestDTO request) {
        return accountsClient.createAccount(request);
    }

    @Override
    public Mono<List<String>> notExistingAccountCurrencyListOfUser() {
        return readAccountsOfUser()
                .map(AccountResponseDTO::getCurrency)
                .collectList()
                .map(existing -> defaultCurrencyList.stream()
                        .filter(item -> !existing.contains(item)).toList());
    }
}
