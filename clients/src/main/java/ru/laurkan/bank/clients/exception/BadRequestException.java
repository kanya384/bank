package ru.laurkan.bank.clients.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String uri, String message) {
        super(String.format("Ошибка запроса %s (Статус: %d - %s)", uri, HttpStatus.BAD_REQUEST.value(), message));
    }
}
