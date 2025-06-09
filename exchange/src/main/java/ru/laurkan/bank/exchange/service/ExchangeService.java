package ru.laurkan.bank.exchange.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.events.exchange.ExchangeRateEventItem;
import ru.laurkan.bank.exchange.dto.ExchangeRateResponseDTO;
import ru.laurkan.bank.exchange.model.Currency;

import java.util.List;

public interface ExchangeService {
    Flux<ExchangeRateResponseDTO> readExchangeRates();

    Mono<ExchangeRateResponseDTO> readExchangeRateByCurrency(Currency currency);

    Flux<ExchangeRateResponseDTO> save(List<ExchangeRateEventItem> exchangeRates);
}
