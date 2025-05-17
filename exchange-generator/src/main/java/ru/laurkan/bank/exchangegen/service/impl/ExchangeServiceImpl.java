package ru.laurkan.bank.exchangegen.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.exchange.ExchangeClient;
import ru.laurkan.bank.clients.exchange.dto.ExchangeRateResponse;
import ru.laurkan.bank.exchangegen.mapper.ExchangeMapper;
import ru.laurkan.bank.exchangegen.model.Currency;
import ru.laurkan.bank.exchangegen.model.ExchangeRate;
import ru.laurkan.bank.exchangegen.service.ExchangeService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    private final ExchangeMapper exchangeMapper;
    private final ExchangeClient exchangeClient;

    @Scheduled(fixedDelay = 1000)
    @Override
    public Flux<ExchangeRateResponse> updateRates() {
        return generateRates()
                .flatMapMany(Flux::fromIterable)
                .map(exchangeMapper::map)
                .collectList()
                .flatMapMany(exchangeClient::update);
    }

    private Mono<List<ExchangeRate>> generateRates() {
        return Flux.just(Currency.values())
                .map(currency -> ExchangeRate.builder()
                        .currency(currency)
                        .rate(generateRateForCurrency(currency))
                        .build())
                .collectList();
    }

    private double generateRateForCurrency(Currency currency) {
        return switch (currency) {
            case RUB -> 1.0;
            case USD -> getRandomNumber(0.01, 0.015);
            case CNY -> getRandomNumber(0.075, 0.085);
        };
    }

    private double getRandomNumber(double min, double max) {
        return ((Math.random() * (max - min)) + min);
    }
}
