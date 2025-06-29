package ru.laurkan.bank.exchange.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.events.exchange.ExchangeRateEventItem;
import ru.laurkan.bank.exchange.dto.ExchangeRateResponseDTO;
import ru.laurkan.bank.exchange.mapper.ExchangeMapper;
import ru.laurkan.bank.exchange.model.Currency;
import ru.laurkan.bank.exchange.model.ExchangeRate;
import ru.laurkan.bank.exchange.repository.ExchangeRepository;
import ru.laurkan.bank.exchange.service.ExchangeService;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {
    private final ExchangeMapper exchangeMapper;
    private final ExchangeRepository exchangeRepository;

    @Override
    public Flux<ExchangeRateResponseDTO> readExchangeRates() {
        return exchangeRepository.findAll()
                .map(exchangeMapper::map);
    }

    @Override
    public Mono<ExchangeRateResponseDTO> readExchangeRateByCurrency(Currency currency) {
        return exchangeRepository.findByCurrency(currency)
                .map(exchangeMapper::map);
    }

    @Override
    public Flux<ExchangeRateResponseDTO> save(List<ExchangeRateEventItem> exchangeRates) {
        log.debug("exchange rates update request received: {}", exchangeRates);
        return Flux.fromIterable(exchangeRates)
                .map(exchangeMapper::map)
                .collectList()
                .flatMap(this::setIdsForExistingRates)
                .flatMapMany(exchangeRepository::saveAll)
                .doOnError(e -> {
                    log.error("error store exchange rate {}", e.getMessage());
                })
                .map(exchangeMapper::map)
                .doOnNext(exchangeRateResponseDTO -> {
                    log.info("exchange rate saved {}", exchangeRateResponseDTO);
                });
    }

    private Mono<List<ExchangeRate>> setIdsForExistingRates(List<ExchangeRate> exchangeRates) {
        return exchangeRepository.findAll()
                .collectMap(ExchangeRate::getCurrency, rate -> rate)
                .map(currencyToIdMap -> {
                    exchangeRates.forEach(rate -> {
                        if (currencyToIdMap.containsKey(rate.getCurrency())) {
                            var existing = currencyToIdMap.get(rate.getCurrency());
                            rate.setId(existing.getId());
                            rate.setCreatedAt(existing.getCreatedAt());
                            rate.setModifiedAt(existing.getModifiedAt());
                        }
                    });
                    return exchangeRates;
                });


    }
}
