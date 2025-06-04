package ru.laurkan.bank.cash.exception;

public class UnsupportedTransactionType extends RuntimeException {
    public UnsupportedTransactionType(String message) {
        super(message);
    }
}
