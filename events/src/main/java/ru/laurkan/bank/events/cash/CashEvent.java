package ru.laurkan.bank.events.cash;

import ru.laurkan.bank.events.common.TransactionEventStatus;

public record CashEvent(
        CashEventType eventType,
        Long accountId,
        Double amount,
        TransactionEventStatus status
) {
}
