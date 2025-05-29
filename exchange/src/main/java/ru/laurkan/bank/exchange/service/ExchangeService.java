package ru.laurkan.bank.exchange.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.exchange.dto.ExchangeRateResponseDTO;
import ru.laurkan.bank.exchange.model.Currency;
import ru.laurkan.bank.exchange.model.ExchangeRate;

import java.util.List;

public interface ExchangeService {
    Flux<ExchangeRateResponseDTO> readExchangeRates();

    Mono<ExchangeRateResponseDTO> readExchangeRateByCurrency(Currency currency);

    Flux<ExchangeRateResponseDTO> save(List<ExchangeRate> exchangeRates);
}
