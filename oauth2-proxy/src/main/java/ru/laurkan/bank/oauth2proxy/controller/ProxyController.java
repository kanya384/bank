package ru.laurkan.bank.oauth2proxy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.oauth2proxy.service.ProxyService;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ProxyController {
    private final ProxyService proxyService;

    @RequestMapping(value = "/**")
    public Mono<ResponseEntity<byte[]>> handleProxyRequest(ProxyExchange<byte[]> proxy) {
        return proxyService.doProxy(proxy);
    }
}
