package ru.laurkan.bank.front.client.accounts;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.client.accounts.dto.accounts.AccountResponseDTO;
import ru.laurkan.bank.front.client.accounts.dto.accounts.CreateAccountRequestDTO;
import ru.laurkan.bank.front.client.accounts.dto.user.*;
import ru.laurkan.bank.front.exception.RegistrationException;


@Component
@RequiredArgsConstructor
public class AccountsClient {
    @Value("${clients.accounts.uri}")
    private String BASE_URL;
    private final WebClient webClient;

    public Mono<UserResponseDTO> findByLogin(FindByLoginRequestDTO requestDTO) {
        return webClient.post()
                .uri(BASE_URL + "/user/find-by-login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(UserResponseDTO.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<UserResponseDTO> registerUser(RegisterUserRequestDTO requestDTO) {
        return webClient.post()
                .uri(BASE_URL + "/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .retrieve()
                .onStatus(
                        HttpStatus.BAD_REQUEST::equals,
                        resp -> resp.bodyToMono(String.class).map(RegistrationException::new)
                )
                .bodyToMono(UserResponseDTO.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<UserResponseDTO> updateUser(Long userId, UpdateUserRequestDTO requestDTO) {
        return webClient.put()
                .uri(BASE_URL + "/user/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(UserResponseDTO.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<UserResponseDTO> changePassword(Long userId, ChangePasswordRequestDTO requestDTO) {
        return webClient.put()
                .uri(BASE_URL + "/user/change-password/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(UserResponseDTO.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Mono<AccountResponseDTO> createAccount(CreateAccountRequestDTO requestDTO) {
        return webClient
                .post()
                .uri(BASE_URL + "/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(AccountResponseDTO.class)
                .switchIfEmpty(Mono.error(new ServerWebInputException("Request body cannot be empty.")));
    }

    public Flux<AccountResponseDTO> readAccountsOfUser(Long userId) {
        return webClient
                .get()
                .uri(BASE_URL + "/accounts/" + userId)
                .retrieve()
                .bodyToFlux(AccountResponseDTO.class);
    }

    public Mono<Void> deleteAccount(Long accountId) {
        return webClient
                .delete()
                .uri(BASE_URL + "/accounts/" + accountId)
                .retrieve()
                .bodyToMono(Void.class);
    }

}
