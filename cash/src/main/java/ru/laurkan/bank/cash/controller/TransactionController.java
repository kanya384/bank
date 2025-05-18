package ru.laurkan.bank.cash.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.cash.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.cash.dto.TransactionResponseDTO;
import ru.laurkan.bank.cash.model.TransactionType;
import ru.laurkan.bank.cash.service.TransactionService;
import ru.laurkan.bank.cash.validators.ValidTransactionType;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/{type}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionResponseDTO> create(@PathVariable("type") @ValidTransactionType String transactionType,
                                               @RequestBody @Valid CreateTransactionRequestDTO request) {
        var type = TransactionType.valueOf(transactionType.toUpperCase());
        return transactionService.create(type, request);
    }
}
