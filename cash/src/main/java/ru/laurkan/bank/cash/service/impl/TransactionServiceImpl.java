package ru.laurkan.bank.cash.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.cash.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.cash.dto.TransactionResponseDTO;
import ru.laurkan.bank.cash.mapper.TransactionMapper;
import ru.laurkan.bank.cash.model.TransactionStatus;
import ru.laurkan.bank.cash.model.TransactionType;
import ru.laurkan.bank.cash.repository.TransactionRepository;
import ru.laurkan.bank.cash.service.TransactionService;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Mono<TransactionResponseDTO> create(TransactionType transactionType, CreateTransactionRequestDTO request) {
        var transaction = transactionMapper.map(transactionType, request);
        return Mono.just(transaction)
                .map(transactionMapper::mapToDb)
                .flatMap(transactionRepository::save)
                .map(transactionMapper::map)
                .map(transactionMapper::map);
    }

    @Override
    public Mono<Void> validateCreatedTransactions() {
        return transactionRepository.findByTransactionStatus(TransactionStatus.CREATED)
                .then();
    }

    @Override
    public Mono<Void> finalizeApprovedTransactions() {
        return null;
    }

    @Override
    public Mono<Void> sendNotifications() {
        return null;
    }

    @Override
    public Flux<TransactionResponseDTO> findAll() {
        return null;
    }
}
