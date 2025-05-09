package ru.laurkan.bank.accounts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.accounts.dto.account.*;
import ru.laurkan.bank.accounts.service.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountsController {
    private final AccountService accountService;

    @GetMapping("/{id}")
    public Mono<AccountResponseDTO> readAccountById(@PathVariable("id") Long accountId) {
        return accountService.readAccountById(accountId);
    }

    @GetMapping("/user/{user-id}")
    public Flux<AccountResponseDTO> readAccountsOfUser(@PathVariable("user-id") Long userId) {
        return accountService.readAccountsOfUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AccountResponseDTO> createAccount(@RequestBody @Valid CreateAccountRequestDTO request) {
        return accountService.create(request);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteAccount(@PathVariable Long id) {
        return accountService.deleteAccount(id);
    }

    @PutMapping("/{id}/put-money")
    public Mono<AccountResponseDTO> putMoneyToAccount(@PathVariable Long id,
                                                      @RequestBody @Valid PutMoneyToAccountDTO putMoneyToAccountDTO) {
        return accountService.putMoneyToAccount(id, putMoneyToAccountDTO.getAmount());
    }

    @PutMapping("/{id}/take-money")
    public Mono<AccountResponseDTO> takeMoneyFromAccount(@PathVariable Long id,
                                                         @RequestBody @Valid TakeMoneyFromAccountDTO takeMoneyFromAccountDTO) {
        return accountService.takeMoneyFromAccount(id, takeMoneyFromAccountDTO.getAmount());
    }

    @PutMapping("/transfer-money")
    public Mono<TransferMoneyResponseDTO> transferMoney(@RequestBody @Valid TransferMoneyRequestDTO transferMoneyRequestDTO) {
        return accountService.transferMoney(transferMoneyRequestDTO);
    }
}
