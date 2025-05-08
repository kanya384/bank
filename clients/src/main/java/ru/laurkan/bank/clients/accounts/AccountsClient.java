package ru.laurkan.bank.clients.accounts;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.accounts.dto.accounts.AccountResponse;
import ru.laurkan.bank.clients.accounts.dto.accounts.CreateAccountRequest;
import ru.laurkan.bank.clients.accounts.dto.user.*;
import ru.laurkan.bank.clients.accounts.exception.RegistrationException;


public class AccountsClient {
    private final String baseUrl;
    private final WebClient webClient;

    public AccountsClient(String baseUrl, WebClient webClient) {
        this.baseUrl = baseUrl;
        this.webClient = webClient;
    }


    public Mono<UserResponse> findByLogin(FindByLoginRequest request) {
        return webClient.post()
                .uri(baseUrl + "/user/find-by-login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<UserResponse> registerUser(RegisterUserRequest request) {
        return webClient.post()
                .uri(baseUrl + "/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        HttpStatus.BAD_REQUEST::equals,
                        resp -> resp.bodyToMono(String.class).map(RegistrationException::new)
                )
                .bodyToMono(UserResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<UserResponse> updateUser(Long userId, UpdateUserRequest request) {
        return webClient.put()
                .uri(baseUrl + "/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<UserResponse> changePassword(Long userId, ChangePasswordRequest request) {
        return webClient.put()
                .uri(baseUrl + "/user/change-password/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<AccountResponse> createAccount(CreateAccountRequest request) {
        return webClient
                .post()
                .uri(baseUrl + "/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Flux<AccountResponse> readAccountsOfUser(Long userId) {
        return webClient
                .get()
                .uri(baseUrl + "/accounts/" + userId)
                .retrieve()
                .bodyToFlux(AccountResponse.class);
    }

    public Mono<Void> deleteAccount(Long accountId) {
        return webClient
                .delete()
                .uri(baseUrl + "/accounts/" + accountId)
                .retrieve()
                .bodyToMono(Void.class);
    }

}
