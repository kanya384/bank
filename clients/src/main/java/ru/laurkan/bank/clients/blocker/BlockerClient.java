package ru.laurkan.bank.clients.blocker;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.blocker.dto.*;

public class BlockerClient {
    private final String baseUrl;
    private final WebClient webClient;

    public BlockerClient(String baseUrl, WebClient webClient) {
        this.baseUrl = baseUrl;
        this.webClient = webClient;
    }

    public Mono<DecisionResponse> validate(DepositTransactionRequest request) {
        return webClient.post()
                .uri(baseUrl + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DecisionResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<DecisionResponse> validate(WithdrawalTransactionRequest request) {
        return webClient.post()
                .uri(baseUrl + "/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DecisionResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<DecisionResponse> validate(SelfTransferTransactionRequest request) {
        return webClient.post()
                .uri(baseUrl + "/transfer-self")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DecisionResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<DecisionResponse> validate(TransferToOtherUserTransactionRequest request) {
        return webClient.post()
                .uri(baseUrl + "/transfer-to-other-user")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DecisionResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }
}
