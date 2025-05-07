package ru.laurkan.bank.notifications;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@TestConfiguration
public class SecurityTestConfiguration {
    @Bean
    @Primary
    public SecurityWebFilterChain securityWebFilterChains(ServerHttpSecurity httpSecurity) {
        return httpSecurity
                .authorizeExchange(authorize -> authorize
                        .anyExchange().permitAll()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

}
