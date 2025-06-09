package ru.laurkan.bank.events.accounts;

public record AccountInfo(
        Long id,
        Long userId,
        String currency,
        Double amount
) {
}
