package ru.laurkan.bank.accounts.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.accounts.dto.account.AccountResponseDTO;
import ru.laurkan.bank.accounts.dto.account.CreateAccountRequestDTO;
import ru.laurkan.bank.accounts.dto.account.TransferMoneyRequestDTO;
import ru.laurkan.bank.accounts.dto.account.TransferMoneyResponseDTO;

public interface AccountService {
    Mono<AccountResponseDTO> create(CreateAccountRequestDTO createAccountRequestDTO);

    Mono<Void> deleteAccount(Long accountId);

    Mono<AccountResponseDTO> readAccountById(Long accountId);

    Flux<AccountResponseDTO> readAccountsOfUser(Long userId);

    Mono<AccountResponseDTO> putMoneyToAccount(Long accountId, Double amount);

    Mono<AccountResponseDTO> takeMoneyFromAccount(Long accountId, Double amount);

    Mono<TransferMoneyResponseDTO> transferMoney(TransferMoneyRequestDTO request);
}
