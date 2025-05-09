package ru.laurkan.bank.exchangegen.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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
public class ExchangeServiceImpl implements ExchangeService {
    private final ExchangeMapper exchangeMapper;
    private final ExchangeClient exchangeClient;

    @Autowired
    public ExchangeServiceImpl(@Value("${clients.exchange.uri}") String exchangeUri,
                               WebClient webClient,
                               ExchangeMapper exchangeMapper
    ) {
        this.exchangeMapper = exchangeMapper;
        this.exchangeClient = new ExchangeClient(exchangeUri, webClient);
    }

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

    public double getRandomNumber(double min, double max) {
        return ((Math.random() * (max - min)) + min);
    }
}
