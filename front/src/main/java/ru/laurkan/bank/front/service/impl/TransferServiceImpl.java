package ru.laurkan.bank.front.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.transfer.TransferClient;
import ru.laurkan.bank.clients.transfer.dto.TransactionResponse;
import ru.laurkan.bank.clients.transfer.dto.TransactionType;
import ru.laurkan.bank.front.dto.transfer.CreateTransactionRequestDTO;
import ru.laurkan.bank.front.mapper.TransferMapper;
import ru.laurkan.bank.front.service.TransferService;

@Log4j2
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final TransferClient transferClient;
    private final TransferMapper transferMapper;

    @Override
    public Mono<TransactionResponse> selfTransfer(CreateTransactionRequestDTO request) {
        return transferClient
                .createTransaction(TransactionType.SELF_TRANSFER, transferMapper.map(request))
                .doOnError(e -> {
                    log.error("error create self transfer: {}", e.getMessage());
                });
    }

    @Override
    public Mono<TransactionResponse> transferToOtherUser(CreateTransactionRequestDTO request) {
        return transferClient
                .createTransaction(TransactionType.TRANSFER_TO_OTHER_USER, transferMapper.map(request))
                .doOnError(e -> {
                    log.error("error create transfer to other user: {}", e.getMessage());
                });
    }
}
