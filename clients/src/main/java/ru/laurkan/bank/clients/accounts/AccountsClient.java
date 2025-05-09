package ru.laurkan.bank.clients.accounts;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.accounts.dto.accounts.*;
import ru.laurkan.bank.clients.accounts.dto.user.*;
import ru.laurkan.bank.clients.accounts.exception.MoneyException;
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

    public Mono<UserResponse> findUserByAccountId(Long accountId) {
        return webClient.get()
                .uri(baseUrl + "/user/find-by-account-id/" + accountId)
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

    public Mono<AccountResponse> readAccountById(Long accountId) {
        return webClient
                .get()
                .uri(baseUrl + "/accounts/" + accountId)
                .retrieve()
                .bodyToMono(AccountResponse.class);
    }

    public Flux<AccountResponse> readAccountsOfUser(Long userId) {
        return webClient
                .get()
                .uri(baseUrl + "/accounts/user/" + userId)
                .retrieve()
                .bodyToFlux(AccountResponse.class);
    }

    public Mono<AccountResponse> putMoneyToAccount(Long accountId, PutMoneyToAccount request) {
        return webClient
                .put()
                .uri(baseUrl + "/accounts/" + accountId + "/put-money")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<AccountResponse> takeMoneyFromAccount(Long accountId, TakeMoneyFromAccount request) {
        return webClient
                .put()
                .uri(baseUrl + "/accounts/" + accountId + "/take-money")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        HttpStatus.PAYMENT_REQUIRED::equals,
                        response -> response.bodyToMono(String.class).map(MoneyException::new)
                )
                .bodyToMono(AccountResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<TransferMoneyResponse> transferMoney(TransferMoneyRequest request) {
        return webClient
                .put()
                .uri(baseUrl + "/accounts/transfer-money")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        HttpStatus.PAYMENT_REQUIRED::equals,
                        response -> response.bodyToMono(String.class).map(MoneyException::new)
                )
                .bodyToMono(TransferMoneyResponse.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<Void> deleteAccount(Long accountId) {
        return webClient
                .delete()
                .uri(baseUrl + "/accounts/" + accountId)
                .retrieve()
                .bodyToMono(Void.class);
    }

}
