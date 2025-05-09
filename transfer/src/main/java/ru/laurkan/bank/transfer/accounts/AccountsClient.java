package ru.laurkan.bank.transfer.accounts;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.transfer.accounts.dto.accounts.AccountResponseDTO;


@Component
@RequiredArgsConstructor
public class AccountsClient {
    @Value("${clients.accounts.uri}")
    private String BASE_URL;
    private final WebClient webClient;

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
