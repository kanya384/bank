package ru.laurkan.bank.front.service;

import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.dto.cash.CashTransactionResponseDTO;
import ru.laurkan.bank.front.dto.cash.CreateCashTransactionRequestDTO;

public interface CashService {
    Mono<CashTransactionResponseDTO> deposit(CreateCashTransactionRequestDTO request);

    Mono<CashTransactionResponseDTO> withdrawal(CreateCashTransactionRequestDTO request);
}
