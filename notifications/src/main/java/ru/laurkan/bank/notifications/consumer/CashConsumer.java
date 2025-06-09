package ru.laurkan.bank.notifications.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.events.cash.CashEventDetailed;
import ru.laurkan.bank.notifications.dto.EmailNotificationResponseDTO;
import ru.laurkan.bank.notifications.service.EmailNotificationService;

import static ru.laurkan.bank.notifications.configuration.KafkaConfiguration.CASH_INPUT_EVENTS_TOPIC;

@Component
@RequiredArgsConstructor
public class CashConsumer {
    private final EmailNotificationService emailNotificationService;

    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 8000),
            dltTopicSuffix = "-dlt",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = CASH_INPUT_EVENTS_TOPIC)
    public Mono<EmailNotificationResponseDTO> processTransferEvent(CashEventDetailed cashEventDetailed) {
        return emailNotificationService.create(cashEventDetailed);
    }
}
