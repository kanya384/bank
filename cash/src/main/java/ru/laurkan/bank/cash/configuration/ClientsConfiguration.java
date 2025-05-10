package ru.laurkan.bank.cash.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.laurkan.bank.clients.accounts.AccountsClient;
import ru.laurkan.bank.clients.blocker.BlockerClient;
import ru.laurkan.bank.clients.notification.NotificationClient;

@Configuration
public class ClientsConfiguration {
    @Value("${clients.accounts.uri}")
    private String accountsUri;
    @Value("${clients.blocker.uri}")
    String blockerUri;
    @Value("${clients.notifications.uri}")
    String notificationsUri;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .build();
    }

    @Bean
    public AccountsClient accountsClient() {
        return new AccountsClient(accountsUri, webClient());
    }

    @Bean
    public BlockerClient blockerClient() {
        return new BlockerClient(blockerUri, webClient());
    }

    @Bean
    public NotificationClient notificationClient() {
        return new NotificationClient(notificationsUri, webClient());
    }
}
