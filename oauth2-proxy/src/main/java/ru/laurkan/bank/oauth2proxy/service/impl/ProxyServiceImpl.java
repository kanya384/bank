package ru.laurkan.bank.oauth2proxy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.oauth2proxy.configuration.ProxyConfigurations;
import ru.laurkan.bank.oauth2proxy.exception.ClientNotDefinedException;
import ru.laurkan.bank.oauth2proxy.service.ProxyService;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyServiceImpl implements ProxyService {
    private final ProxyConfigurations proxyConfigurations;

    private final ReactiveOAuth2AuthorizedClientManager manager;
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Override
    public Mono<ResponseEntity<byte[]>> doProxy(ProxyExchange<byte[]> proxy) {
        String clientId = getRootPath(proxy.path());
        var destinationUri = getDestinationUri(proxy.path(), proxyConfigurations.getClients().values()
                .stream()
                .filter(client -> client.clientId().equals(clientId))
                .findFirst()
                .orElseThrow(() -> new ClientNotDefinedException("not found client with id = " + clientId))
                .upstream());
        log.info("proxying request to: {}", destinationUri);
        return getTokenForClient(clientId)
                .flatMap(accessToken -> proxy
                        .uri(destinationUri)
                        .header("Authorization", "Bearer " + accessToken)
                        .forward()
                );
    }

    private Mono<String> getTokenForClient(String clientId) {
        OAuth2AuthorizeRequest req = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientId)
                .principal("N/A")
                .build();

        return manager.authorize(req)
                .map(client -> client.getAccessToken().getTokenValue());
    }

    private String getRootPath(String path) {
        String[] items = path.split("/");
        if (items.length <= 1) {
            return "";
        }

        return items[1];
    }

    private String getDestinationUri(String incomingPath, String upstream) {
        return upstream + "/" + Arrays.stream(incomingPath.split("/")).skip(2).collect(Collectors.joining("/"));
    }
}
