package ru.laurkan.bank.transfer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.transfer.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.transfer.dto.TransactionResponseDTO;
import ru.laurkan.bank.transfer.model.TransactionType;
import ru.laurkan.bank.transfer.service.TransactionService;
import ru.laurkan.bank.transfer.validators.ValidTransactionType;

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
