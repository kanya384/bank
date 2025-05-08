package ru.laurkan.bank.cash.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.cash.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.cash.dto.TransactionResponseDTO;
import ru.laurkan.bank.cash.model.TransactionType;

public interface TransactionService {
    Mono<TransactionResponseDTO> create(TransactionType transactionType, CreateTransactionRequestDTO request);
    
    Mono<Void> validateCreatedTransactions();

    Mono<Void> finalizeApprovedTransactions();

    Mono<Void> sendNotifications();

    Flux<TransactionResponseDTO> findAll();
}
