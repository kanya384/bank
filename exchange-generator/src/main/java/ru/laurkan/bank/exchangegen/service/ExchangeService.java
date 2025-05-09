package ru.laurkan.bank.exchangegen.service;

import reactor.core.publisher.Flux;
import ru.laurkan.bank.clients.exchange.dto.ExchangeRateResponse;

public interface ExchangeService {
    Flux<ExchangeRateResponse> updateRates();
}
