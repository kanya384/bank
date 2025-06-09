package ru.laurkan.bank.accounts.exception;

public class SendEventException extends RuntimeException {
    public SendEventException(String message) {
        super(message);
    }
}
