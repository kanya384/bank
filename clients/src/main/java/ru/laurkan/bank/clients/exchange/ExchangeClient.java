package ru.laurkan.bank.clients.exchange;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.ClientBase;
import ru.laurkan.bank.clients.exchange.dto.Currency;
import ru.laurkan.bank.clients.exchange.dto.ExchangeRateResponse;
import ru.laurkan.bank.clients.exchange.dto.UpdateExchangeRateRequest;

import java.util.List;

@RequiredArgsConstructor
public class ExchangeClient extends ClientBase {
    private final String baseUrl;
    private final WebClient webClient;

    public Flux<ExchangeRateResponse> readAll() {
        return Flux.just(webClient.get()
                        .uri(baseUrl + "/exchange-rates")
                        .retrieve())
                .flatMap(responseSpec -> responseToFlux(responseSpec, ExchangeRateResponse.class));
    }

    public Mono<ExchangeRateResponse> readByCurrency(Currency currency) {
        return Mono.just(webClient.get()
                        .uri(baseUrl + "/exchange-rates/" + currency)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, ExchangeRateResponse.class));
    }

    public Flux<ExchangeRateResponse> update(List<UpdateExchangeRateRequest> request) {
        return Flux.just(webClient.post()
                        .uri(baseUrl + "/exchange-rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToFlux(responseSpec, ExchangeRateResponse.class));
    }
}
