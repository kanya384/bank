package ru.laurkan.bank.clients;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.exception.BadRequestException;
import ru.laurkan.bank.clients.exception.InternalServerRequestException;

public abstract class ClientBase {
    protected <T> Mono<T> responseToMono(WebClient.ResponseSpec responseSpec, Class<T> responseClass) {
        return processErrors(responseSpec)
                .bodyToMono(responseClass);
    }

    protected <T> Flux<T> responseToFlux(WebClient.ResponseSpec responseSpec, Class<T> responseClass) {
        return processErrors(responseSpec)
                .bodyToFlux(responseClass);
    }

    private WebClient.ResponseSpec processErrors(WebClient.ResponseSpec responseSpec) {
        return responseSpec
                .onStatus(
                        HttpStatus.BAD_REQUEST::equals,
                        resp -> resp.bodyToMono(ErrorResponse.class)
                                .map(e -> new BadRequestException(resp.request().getURI().toString(),
                                        e.getMessage()))
                )
                .onStatus(
                        HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        resp -> resp.bodyToMono(String.class)
                                .map(e -> new InternalServerRequestException(resp.request().getURI().toString(),
                                        e))
                );
    }
}
