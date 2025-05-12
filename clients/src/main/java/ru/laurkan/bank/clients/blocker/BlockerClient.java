package ru.laurkan.bank.clients.blocker;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.ClientBase;
import ru.laurkan.bank.clients.blocker.dto.*;

@RequiredArgsConstructor
public class BlockerClient extends ClientBase {
    private final String baseUrl;
    private final WebClient webClient;

    public Mono<DecisionResponse> validate(DepositTransactionRequest request) {
        return Mono.just(webClient.post()
                        .uri(baseUrl + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, DecisionResponse.class));
    }

    public Mono<DecisionResponse> validate(WithdrawalTransactionRequest request) {
        return Mono.just(webClient.post()
                        .uri(baseUrl + "/withdrawal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, DecisionResponse.class));
    }

    public Mono<DecisionResponse> validate(SelfTransferTransactionRequest request) {
        return Mono.just(webClient.post()
                        .uri(baseUrl + "/transfer-self")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, DecisionResponse.class));
    }

    public Mono<DecisionResponse> validate(TransferToOtherUserTransactionRequest request) {
        return Mono.just(webClient.post()
                        .uri(baseUrl + "/transfer-to-other-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, DecisionResponse.class));
    }
}
