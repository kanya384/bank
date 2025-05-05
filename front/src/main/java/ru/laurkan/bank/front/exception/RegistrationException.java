package ru.laurkan.bank.front.exception;

public class RegistrationException extends RuntimeException {
    public RegistrationException(String message) {
        super(message);
    }
}
