package ru.laurkan.bank.gateway.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

@Component
public class TokenRelayKeycloakGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenRelayKeycloakGatewayFilterFactory.Config> {
    private final ReactiveOAuth2AuthorizedClientManager manager;

    @Autowired
    public TokenRelayKeycloakGatewayFilterFactory(ReactiveOAuth2AuthorizedClientManager manager) {
        super(Config.class);
        this.manager = manager;
    }

    @Override
    public GatewayFilter apply(Config config) {
        OAuth2AuthorizeRequest req = OAuth2AuthorizeRequest
                .withClientRegistrationId(config.registrationId)
                .principal("N/A")
                .build();

        return ((exchange, chain) -> manager.authorize(req)
                .flatMap(client -> chain.filter(
                        exchange.mutate().request(
                                        exchange.getRequest().mutate()
                                                .header("Authorization", "Bearer " + client.getAccessToken().getTokenValue())
                                                .build()
                                )
                                .build())));
    }


    @Setter
    @Getter
    public static class Config {

        private String registrationId;

    }
}
