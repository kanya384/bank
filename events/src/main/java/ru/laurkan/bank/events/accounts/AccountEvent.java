package ru.laurkan.bank.events.accounts;

public record AccountEvent(
        AccountEventType eventType,
        Long accountId
) {
}