package ru.laurkan.bank.exchangegen.service;

import reactor.core.publisher.Flux;
import ru.laurkan.bank.exchangegen.dto.ExchangeRateResponseDTO;

public interface ExchangeService {
    Flux<ExchangeRateResponseDTO> readRates();
}
