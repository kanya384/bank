package ru.laurkan.bank.front.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.laurkan.bank.clients.exchange.ExchangeClient;
import ru.laurkan.bank.front.dto.exchange.ExchangeRateResponseDTO;
import ru.laurkan.bank.front.mapper.ExchangeMapper;
import ru.laurkan.bank.front.service.ExchangeService;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    private final ExchangeClient exchangeClient;
    private final ExchangeMapper exchangeMapper;

    @Override
    public Flux<ExchangeRateResponseDTO> readRates() {
        return exchangeClient.readAll()
                .map(exchangeMapper::map);
    }
}
