package ru.laurkan.bank.oauth2proxy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.oauth2proxy.service.ProxyService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyServiceImpl implements ProxyService {
    @Value("${upstream-uri}")
    private String upstreamUri;

    private final ReactiveOAuth2AuthorizedClientManager manager;
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Override
    public Mono<ResponseEntity<byte[]>> doProxy(ProxyExchange<byte[]> proxy) {
        return getTokenForClient()
                .flatMap(accessToken -> proxy
                        .uri(upstreamUri + proxy.path())
                        .header("Authorization", "Bearer " + accessToken)
                        .forward()
                );
    }

    private Mono<String> getTokenForClient() {
        OAuth2AuthorizeRequest req = OAuth2AuthorizeRequest
                .withClientRegistrationId("default")
                .principal("N/A")
                .build();

        return manager.authorize(req)
                .map(client -> client.getAccessToken().getTokenValue());
    }
}
