package ru.laurkan.bank.exchange.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.exchange.model.Currency;
import ru.laurkan.bank.exchange.model.ExchangeRate;

@Repository
public interface ExchangeRepository extends ReactiveCrudRepository<ExchangeRate, Long> {
    Mono<ExchangeRate> findByCurrency(Currency currency);
}
