package ru.laurkan.bank.transfer.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.transfer.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.transfer.dto.TransactionResponseDTO;
import ru.laurkan.bank.transfer.mapper.TransactionMapper;
import ru.laurkan.bank.transfer.model.TransactionType;
import ru.laurkan.bank.transfer.repository.TransactionRepository;
import ru.laurkan.bank.transfer.service.TransactionService;

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

}
