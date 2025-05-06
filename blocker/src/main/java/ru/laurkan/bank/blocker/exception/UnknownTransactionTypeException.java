package ru.laurkan.bank.blocker.exception;

public class UnknownTransactionTypeException extends RuntimeException {
    public UnknownTransactionTypeException(String name) {
        super("Неизвестный тип трпнзакции: " + name);
    }
}
