package ru.laurkan.bank.oauth2proxy.exception;

public class ClientNotDefinedException extends RuntimeException {
    public ClientNotDefinedException(String clientId) {
        super(String.format("Не задан клиент для проксирования (%s)", clientId));
    }
}
