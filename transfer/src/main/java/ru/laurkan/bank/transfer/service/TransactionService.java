package ru.laurkan.bank.transfer.service;

import reactor.core.publisher.Mono;
import ru.laurkan.bank.transfer.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.transfer.dto.TransactionResponseDTO;
import ru.laurkan.bank.transfer.model.TransactionType;

public interface TransactionService {
    Mono<TransactionResponseDTO> create(TransactionType transactionType, CreateTransactionRequestDTO request);
}
