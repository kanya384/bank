package ru.laurkan.bank.events.transfer;

import ru.laurkan.bank.events.common.TransactionEventStatus;

public record TransferEvent(
        TransferEventType eventType,
        Long fromAccountId,
        Double amount,
        Long toAccountId,
        TransactionEventStatus status
) {
}
