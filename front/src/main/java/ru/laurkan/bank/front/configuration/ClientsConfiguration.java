package ru.laurkan.bank.front.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.laurkan.bank.clients.accounts.AccountsClient;

@Configuration
public class ClientsConfiguration {
    @Value("${clients.accounts.uri}")
    private String accountsUri;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .build();
    }

    @Bean
    public AccountsClient accountsClient() {
        return new AccountsClient(accountsUri, webClient());
    }
}
