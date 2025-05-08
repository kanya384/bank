package ru.laurkan.bank.front.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.accounts.AccountsClient;
import ru.laurkan.bank.front.dto.account.AccountResponseDTO;
import ru.laurkan.bank.front.dto.account.CreateAccountRequestDTO;
import ru.laurkan.bank.front.mapper.AccountMapper;
import ru.laurkan.bank.front.service.AccountsService;
import ru.laurkan.bank.front.utils.SecurityUtils;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountsService {
    private final AccountsClient accountsClient;
    private final AccountMapper accountMapper;

    private final List<String> defaultCurrencyList = List.of("USD", "RUB", "CNY");

    @Autowired
    public AccountServiceImpl(@Value("${clients.accounts.uri}") String accountClientUrl,
                              WebClient webClient,
                              AccountMapper accountMapper) {
        this.accountsClient = new AccountsClient(accountClientUrl, webClient);
        this.accountMapper = accountMapper;
    }


    @Override
    public Flux<AccountResponseDTO> readAccountsOfUser() {
        return SecurityUtils.getUserId()
                .flatMapMany(accountsClient::readAccountsOfUser)
                .map(accountMapper::map);
    }

    @Override
    public Mono<Void> deleteAccount(Long userId) {
        return accountsClient.deleteAccount(userId);
    }

    @Override
    @PreAuthorize("#request.userId == authentication.principal.id")
    public Mono<AccountResponseDTO> createAccount(CreateAccountRequestDTO request) {
        return accountsClient.createAccount(accountMapper.map(request))
                .map(accountMapper::map);
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
