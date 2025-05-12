package ru.laurkan.bank.clients.accounts;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.ClientBase;
import ru.laurkan.bank.clients.accounts.dto.accounts.*;
import ru.laurkan.bank.clients.accounts.dto.user.*;
import ru.laurkan.bank.clients.accounts.exception.MoneyException;

@RequiredArgsConstructor
public class AccountsClient extends ClientBase {
    private final String baseUrl;
    private final WebClient webClient;


    public Mono<UserResponse> findByLogin(FindByLoginRequest request) {
        return Mono.just(webClient.post()
                        .uri(baseUrl + "/user/find-by-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, UserResponse.class));
    }

    public Mono<UserResponse> findUserByAccountId(Long accountId) {
        return Mono.just(webClient.get()
                        .uri(baseUrl + "/user/find-by-account-id/" + accountId)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, UserResponse.class));
    }

    public Mono<UserResponse> registerUser(RegisterUserRequest request) {
        return Mono.just(webClient.post()
                        .uri(baseUrl + "/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, UserResponse.class));
    }

    public Mono<UserResponse> updateUser(Long userId, UpdateUserRequest request) {
        return Mono.just(webClient.put()
                        .uri(baseUrl + "/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, UserResponse.class));
    }

    public Mono<UserResponse> changePassword(Long userId, ChangePasswordRequest request) {
        return Mono.just(webClient.put()
                        .uri(baseUrl + "/user/change-password/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, UserResponse.class));
    }

    public Flux<UsersAccountsResponse> readUsersWithAccounts() {
        return Flux.just(webClient
                        .get()
                        .uri(baseUrl + "/user/all-with-accounts")
                        .retrieve())
                .flatMap(responseSpec -> responseToFlux(responseSpec, UsersAccountsResponse.class));
    }

    public Mono<AccountResponse> createAccount(CreateAccountRequest request) {
        return Mono.just(webClient
                        .post()
                        .uri(baseUrl + "/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, AccountResponse.class));
    }

    public Mono<AccountResponse> readAccountById(Long accountId) {
        return Mono.just(webClient
                        .get()
                        .uri(baseUrl + "/accounts/" + accountId)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, AccountResponse.class));
    }

    public Flux<AccountResponse> readAccountsOfUser(Long userId) {
        return Flux.just(webClient
                        .get()
                        .uri(baseUrl + "/accounts/user/" + userId)
                        .retrieve())
                .flatMap(responseSpec -> responseToFlux(responseSpec, AccountResponse.class));
    }

    public Mono<AccountResponse> putMoneyToAccount(Long accountId, PutMoneyToAccount request) {
        return Mono.just(webClient
                        .put()
                        .uri(baseUrl + "/accounts/" + accountId + "/put-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, AccountResponse.class));
    }

    public Mono<AccountResponse> takeMoneyFromAccount(Long accountId, TakeMoneyFromAccount request) {
        return Mono.just(webClient
                        .put()
                        .uri(baseUrl + "/accounts/" + accountId + "/take-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve()
                        .onStatus(
                                HttpStatus.PAYMENT_REQUIRED::equals,
                                response -> response.bodyToMono(String.class).map(MoneyException::new)
                        ))
                .flatMap(responseSpec -> responseToMono(responseSpec, AccountResponse.class));
    }

    public Mono<TransferMoneyResponse> transferMoney(TransferMoneyRequest request) {
        return Mono.just(webClient
                        .put()
                        .uri(baseUrl + "/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(request)
                        .retrieve()
                        .onStatus(
                                HttpStatus.PAYMENT_REQUIRED::equals,
                                response -> response.bodyToMono(String.class).map(MoneyException::new)
                        ))
                .flatMap(responseSpec -> responseToMono(responseSpec, TransferMoneyResponse.class));
    }

    public Mono<Void> deleteAccount(Long accountId) {
        return Mono.just(webClient
                        .delete()
                        .uri(baseUrl + "/accounts/" + accountId)
                        .retrieve())
                .flatMap(responseSpec -> responseToMono(responseSpec, Void.class));
    }

}
