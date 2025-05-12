package ru.laurkan.bank.clients.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.ClientBase;
import ru.laurkan.bank.clients.transfer.dto.CreateTransactionRequest;
import ru.laurkan.bank.clients.transfer.dto.TransactionResponse;
import ru.laurkan.bank.clients.transfer.dto.TransactionType;

@RequiredArgsConstructor
public class TransferClient extends ClientBase {
    private final String baseUrl;
    private final WebClient webClient;

    public Mono<TransactionResponse> createTransaction(TransactionType transactionType,
                                                       CreateTransactionRequest request) {
        return Mono.just(webClient.post()
                        .uri(baseUrl + "/transaction/" + transactionType.toString().toLowerCase())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, TransactionResponse.class));
    }
}
