package ru.laurkan.bank.transfer.exception;

public class UnknownTransactionTypeException extends RuntimeException {
    public UnknownTransactionTypeException(String message) {
        super(message);
    }
}
