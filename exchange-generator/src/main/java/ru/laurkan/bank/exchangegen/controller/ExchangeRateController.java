package ru.laurkan.bank.exchangegen.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.laurkan.bank.exchangegen.dto.ExchangeRateResponseDTO;
import ru.laurkan.bank.exchangegen.service.ExchangeService;

@RestController
@RequestMapping("/exchange/rates")
@RequiredArgsConstructor
public class ExchangeRateController {
    private final ExchangeService exchangeService;

    @GetMapping
    public Flux<ExchangeRateResponseDTO> read() {
        return exchangeService.readRates();
    }
}
