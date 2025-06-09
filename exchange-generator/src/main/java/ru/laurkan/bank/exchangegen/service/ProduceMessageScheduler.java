package ru.laurkan.bank.exchangegen.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.events.exchange.ExchangeRateEventItem;
import ru.laurkan.bank.events.exchange.UpdateExchangeRateEvent;
import ru.laurkan.bank.exchangegen.model.Currency;
import ru.laurkan.bank.exchangegen.model.ExchangeRate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduceMessageScheduler {
    private final KafkaTemplate<String, UpdateExchangeRateEvent> kafkaTemplate;
    private final String key = "new-rates";
    private final String topic = "rates";

    @Scheduled(fixedDelay = 1000)
    public Mono<Void> updateRates() {
        return generateRates()
                .flatMapMany(Flux::fromIterable)
                .collectList()
                .doOnNext(exchangeRates -> {
                    var rates = new UpdateExchangeRateEvent();
                    List<ExchangeRateEventItem> items = new ArrayList<>();
                    exchangeRates.forEach(rate -> items
                            .add(new ExchangeRateEventItem(rate.getCurrency().toString(), rate.getRate())));
                    rates.setRates(items);
                    kafkaTemplate.send(topic, key, rates);
                })
                .then();
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
