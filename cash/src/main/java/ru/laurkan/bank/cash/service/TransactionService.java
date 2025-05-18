package ru.laurkan.bank.cash.service;

import reactor.core.publisher.Mono;
import ru.laurkan.bank.cash.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.cash.dto.TransactionResponseDTO;
import ru.laurkan.bank.cash.model.TransactionType;

public interface TransactionService {
    Mono<TransactionResponseDTO> create(TransactionType transactionType, CreateTransactionRequestDTO request);
}
