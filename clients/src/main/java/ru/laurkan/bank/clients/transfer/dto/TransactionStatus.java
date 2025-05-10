package ru.laurkan.bank.clients.transfer.dto;

public enum TransactionStatus {
    CREATED,
    APPROVED,
    BLOCKED,
    COMPLETED,
    NOT_ENOUGH_MONEY,
    FAILED
}
