package ru.laurkan.bank.front.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.transfer.dto.TransactionResponse;
import ru.laurkan.bank.front.dto.transfer.CreateTransactionRequestDTO;

@Service
public interface TransferService {
    Mono<TransactionResponse> selfTransfer(CreateTransactionRequestDTO request);

    Mono<TransactionResponse> transferToOtherUser(CreateTransactionRequestDTO request);
}
