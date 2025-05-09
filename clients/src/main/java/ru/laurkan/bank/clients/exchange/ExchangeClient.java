package ru.laurkan.bank.clients.exchange;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.exchange.dto.Currency;
import ru.laurkan.bank.clients.exchange.dto.ExchangeRateResponse;
import ru.laurkan.bank.clients.exchange.dto.UpdateExchangeRateRequest;

import java.util.List;

public class ExchangeClient {
    private final String baseUrl;
    private final WebClient webClient;

    public ExchangeClient(String baseUrl, WebClient webClient) {
        this.baseUrl = baseUrl;
        this.webClient = webClient;
    }

    public Flux<ExchangeRateResponse> readAll() {
        return webClient.get()
                .uri(baseUrl + "/exchange-rates")
                .retrieve()
                .bodyToFlux(ExchangeRateResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<ExchangeRateResponse> readByCurrency(Currency currency) {
        return webClient.get()
                .uri(baseUrl + "/exchange-rates/" + currency)
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Flux<ExchangeRateResponse> update(List<UpdateExchangeRateRequest> request) {
        return webClient.post()
                .uri(baseUrl + "/exchange-rates")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(ExchangeRateResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }
}
