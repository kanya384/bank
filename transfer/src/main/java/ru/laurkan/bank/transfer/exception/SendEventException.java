package ru.laurkan.bank.transfer.exception;

public class SendEventException extends RuntimeException {
    public SendEventException(String message) {
        super(message);
    }
}
