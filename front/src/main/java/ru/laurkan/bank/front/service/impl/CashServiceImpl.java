package ru.laurkan.bank.front.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.cash.CashClient;
import ru.laurkan.bank.clients.cash.dto.TransactionType;
import ru.laurkan.bank.front.dto.cash.CashTransactionResponseDTO;
import ru.laurkan.bank.front.dto.cash.CreateCashTransactionRequestDTO;
import ru.laurkan.bank.front.mapper.CashMapper;
import ru.laurkan.bank.front.service.CashService;

@Log4j2
@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {
    private final CashClient cashClient;
    private final CashMapper cashMapper;

    @Override
    public Mono<CashTransactionResponseDTO> deposit(CreateCashTransactionRequestDTO request) {
        return cashClient.createTransaction(TransactionType.DEPOSIT, cashMapper.map(request))
                .doOnError(e -> {
                    log.error("error create deposit: {}", e.getMessage());
                })
                .map(cashMapper::map);
    }

    @Override
    public Mono<CashTransactionResponseDTO> withdrawal(CreateCashTransactionRequestDTO request) {
        return cashClient.createTransaction(TransactionType.WITHDRAWAL, cashMapper.map(request))
                .doOnError(e -> {
                    log.error("error create withdrawal: {}", e.getMessage());
                })
                .map(cashMapper::map);
    }
}
