package ru.laurkan.bank.exchange.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.exchange.dto.ExchangeRateResponseDTO;
import ru.laurkan.bank.exchange.dto.UpdateExchangeRateRequestDTO;
import ru.laurkan.bank.exchange.model.Currency;
import ru.laurkan.bank.exchange.service.ExchangeService;

import java.util.List;

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

    @PostMapping
    public Flux<ExchangeRateResponseDTO> save(@RequestBody @Valid List<UpdateExchangeRateRequestDTO> request) {
        return exchangeService.save(request);
    }
}
