package ru.laurkan.bank.accounts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.accounts.dto.account.AccountResponseDTO;
import ru.laurkan.bank.accounts.dto.account.CreateAccountRequestDTO;
import ru.laurkan.bank.accounts.service.AccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountsController {
    private final AccountService accountService;

    @GetMapping("/{user-id}")
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
}
