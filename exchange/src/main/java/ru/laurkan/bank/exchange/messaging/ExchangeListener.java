package ru.laurkan.bank.exchange.messaging;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.laurkan.bank.events.exchange.UpdateExchangeRateEvent;
import ru.laurkan.bank.exchange.service.ExchangeService;

@Service
@RequiredArgsConstructor
public class ExchangeListener {
    private final ExchangeService exchangeService;

    @KafkaListener(topics = {"rates"},
            properties = {"spring.json.value.default.type=ru.laurkan.bank.exchange.dto.UpdateExchangeRateRequest"}
    )
    public void listen(@Payload @Valid UpdateExchangeRateEvent data) {
        exchangeService.save(data.getRates())
                .subscribe();
    }
}
