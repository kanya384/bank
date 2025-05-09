package ru.laurkan.bank.transfer.model;

public enum TransactionStatus {
    CREATED,
    APPROVED,
    BLOCKED,
    COMPLETED,
    NOT_ENOUGH_MONEY,
    FAILED
}
