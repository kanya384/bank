package ru.laurkan.bank.clients.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.cash.dto.CreateTransactionRequest;
import ru.laurkan.bank.clients.cash.dto.TransactionResponse;
import ru.laurkan.bank.clients.cash.dto.TransactionType;

@RequiredArgsConstructor
public class TransferClient {
    private final String baseUrl;
    private final WebClient webClient;

    public Mono<TransactionResponse> createTransaction(TransactionType transactionType,
                                                       CreateTransactionRequest request) {
        return webClient.post()
                .uri(baseUrl + "/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TransactionResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }
}
