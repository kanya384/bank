package ru.laurkan.bank.clients.cash;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.ClientBase;
import ru.laurkan.bank.clients.cash.dto.CreateTransactionRequest;
import ru.laurkan.bank.clients.cash.dto.TransactionResponse;
import ru.laurkan.bank.clients.cash.dto.TransactionType;

@RequiredArgsConstructor
public class CashClient extends ClientBase {
    @Value("${clients.cash.uri}")
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
