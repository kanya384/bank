package ru.laurkan.bank.exchangegen.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.exchangegen.dto.ExchangeRateResponseDTO;
import ru.laurkan.bank.exchangegen.mapper.ExchangeMapper;
import ru.laurkan.bank.exchangegen.model.Currency;
import ru.laurkan.bank.exchangegen.model.ExchangeRate;
import ru.laurkan.bank.exchangegen.service.ExchangeService;

import java.util.List;

@Service
public class ExchangeServiceImpl implements ExchangeService {
    private final ExchangeMapper exchangeMapper;
    private volatile List<ExchangeRate> exchangeRates;

    @Autowired
    public ExchangeServiceImpl(ExchangeMapper exchangeMapper) {
        refreshRates().block();
        this.exchangeMapper = exchangeMapper;
    }

    @Scheduled(fixedDelay = 1000)
    private Mono<List<ExchangeRate>> refreshRates() {
        return Flux.just(Currency.values())
                .map(currency -> ExchangeRate.builder()
                        .currency(currency)
                        .rate(generateRateForCurrency(currency))
                        .build())
                .collectList()
                .doOnNext(list -> exchangeRates = list);
    }

    private double generateRateForCurrency(Currency currency) {
        return switch (currency) {
            case RUB -> 1.0;
            case USD -> getRandomNumber(0.15, 0.25);
            case CNY -> getRandomNumber(0.3, 0.5);
        };
    }

    public Flux<ExchangeRateResponseDTO> readRates() {
        return Flux.fromIterable(exchangeRates)
                .map(exchangeMapper::map);
    }

    public double getRandomNumber(double min, double max) {
        return ((Math.random() * (max - min)) + min);
    }
}
