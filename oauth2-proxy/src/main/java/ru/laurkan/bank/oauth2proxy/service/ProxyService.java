package ru.laurkan.bank.oauth2proxy.service;

import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface ProxyService {
    Mono<ResponseEntity<byte[]>> doProxy(ProxyExchange<byte[]> proxy);
}
