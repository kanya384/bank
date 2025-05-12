package ru.laurkan.bank.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.dto.transfer.CreateTransactionRequestDTO;
import ru.laurkan.bank.front.dto.transfer.UserPickRequest;
import ru.laurkan.bank.front.service.AccountsService;
import ru.laurkan.bank.front.service.TransferService;
import ru.laurkan.bank.front.service.UserService;
import ru.laurkan.bank.front.utils.SecurityUtils;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transfer")
public class TransferController {
    private final TransferService transferService;
    private final AccountsService accountsService;
    private final UserService userService;

    @GetMapping
    private Mono<String> transferPage(Model model) {
        model.addAttribute("accounts", accountsService.readAccountsOfUser());
        model.addAttribute("current", SecurityUtils.getUserId());
        model.addAttribute("users", userService.readUsersWithAccounts());
        return Mono.just("transfer");
    }

    @PostMapping
    private Mono<String> transferPageWithSelectedUser(@ModelAttribute UserPickRequest request, Model model) {
        model.addAttribute("accounts", accountsService.readAccountsOfUser());
        model.addAttribute("current", SecurityUtils.getUserId());
        model.addAttribute("users", userService.readUsersWithAccounts());
        model.addAttribute("receiver_id", request.getUser());
        model.addAttribute("receiver_accounts", accountsService.readAccountsOfUserById(request.getUser()));
        return Mono.just("transfer");
    }

    @PostMapping("/self-transfer")
    private Mono<String> selfTransfer(@ModelAttribute CreateTransactionRequestDTO request, Model model) {
        return transferService.selfTransfer(request)
                .flatMap(__ -> Mono.just("redirect:/transfer"));
    }

    @PostMapping("/transfer-to-other-user")
    private Mono<String> transferToOtherUser(@ModelAttribute CreateTransactionRequestDTO request, Model model) {
        return transferService.transferToOtherUser(request)
                .flatMap(__ -> Mono.just("redirect:/transfer"));
    }
}
