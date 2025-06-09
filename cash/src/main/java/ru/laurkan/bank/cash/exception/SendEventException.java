package ru.laurkan.bank.cash.exception;

public class SendEventException extends RuntimeException {
    public SendEventException(String message) {
        super(message);
    }
}
