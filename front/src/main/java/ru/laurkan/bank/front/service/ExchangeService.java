package ru.laurkan.bank.front.service;

import reactor.core.publisher.Flux;
import ru.laurkan.bank.front.dto.exchange.ExchangeRateResponseDTO;

public interface ExchangeService {
    Flux<ExchangeRateResponseDTO> readRates();
}
