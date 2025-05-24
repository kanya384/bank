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
import ru.laurkan.bank.oauth2proxy.exception.ClientNotDefinedException;
import ru.laurkan.bank.oauth2proxy.service.ProxyService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyServiceImpl implements ProxyService {

    private final ReactiveOAuth2AuthorizedClientManager manager;
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Override
    public Mono<ResponseEntity<byte[]>> doProxy(String upstreamUri, ProxyExchange<byte[]> proxy) {
        log.info("Upstream-uri: " + upstreamUri);
        var clientId = getRootPath(proxy.path());
        return clientRegistrationRepository.findByRegistrationId(clientId)
                .switchIfEmpty(Mono.error(new ClientNotDefinedException(clientId)))
                .flatMap(__ -> getTokenForClient(clientId))
                .flatMap(accessToken -> proxy
                        .uri(upstreamUri + proxy.path())
                        .header("Authorization", "Bearer " + accessToken)
                        .forward()
                );
    }

    private String getRootPath(String path) {
        String[] paths = path.split("/");
        if (paths.length < 2) {
            return "";
        }
        return paths[1];
    }

    private Mono<String> getTokenForClient(String clientId) {
        OAuth2AuthorizeRequest req = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientId)
                .principal("N/A")
                .build();

        return manager.authorize(req)
                .map(client -> client.getAccessToken().getTokenValue());
    }
}
