package ru.laurkan.bank.front.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.client.accounts.dto.accounts.CreateAccountRequestDTO;
import ru.laurkan.bank.front.service.AccountsService;
import ru.laurkan.bank.front.utils.SecurityUtils;

@Controller
@RequiredArgsConstructor
public class AccountsController {
    private final AccountsService accountsService;

    @GetMapping
    public Mono<String> readAccountsOfUser(Model model) {
        model.addAttribute("accounts", accountsService.readAccountsOfUser());
        model.addAttribute("userId", SecurityUtils.getUserId());
        model.addAttribute("currencyList", accountsService.notExistingAccountCurrencyListOfUser());
        return Mono.just("accounts");
    }

    @PostMapping("/accounts")
    public Mono<String> createAccount(@ModelAttribute @Valid CreateAccountRequestDTO request) {
        return accountsService.createAccount(request)
                .then(Mono.just("redirect:/"));
    }

    @PostMapping("/accounts/{accountId}/delete")
    public Mono<String> deleteAccount(@PathVariable Long accountId) {
        return accountsService.deleteAccount(accountId)
                .then(Mono.just("redirect:/"));
    }
}
