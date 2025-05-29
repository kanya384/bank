package ru.laurkan.bank.exchange.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.exchange.dto.ExchangeRateResponseDTO;
import ru.laurkan.bank.exchange.model.Currency;
import ru.laurkan.bank.exchange.service.ExchangeService;

@RestController
@RequestMapping("/exchange-rates")
@RequiredArgsConstructor
public class ExchangeRateController {
    private final ExchangeService exchangeService;

    @GetMapping
    public Flux<ExchangeRateResponseDTO> read() {
        return exchangeService.readExchangeRates();
    }

    @GetMapping("/{currency}")
    public Mono<ExchangeRateResponseDTO> readByCurrency(@PathVariable @Valid String currency) {
        return exchangeService.readExchangeRateByCurrency(Currency.valueOf(currency));
    }
}
