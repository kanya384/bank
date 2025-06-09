package ru.laurkan.bank.cash.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.laurkan.bank.cash.exception.SendEventException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleParameterNotValidException(final WebExchangeBindException e) {
        List<FieldError> errors = e.getFieldErrors();
        StringBuffer sb = new StringBuffer();
        errors.forEach(fieldError -> {
            if (!sb.isEmpty()) {
                sb.append("; ");
            }

            sb.append(String.format("%s - %s", fieldError.getField(), fieldError.getDefaultMessage()));
        });
        return ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST)
                .message(String.format("Ошибка(-и) валидации: %s", sb))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleParameterNotValidException(final HandlerMethodValidationException e) {
        var errors = e.getAllErrors();

        StringBuffer sb = new StringBuffer();

        errors.forEach(fieldError -> {
            if (!sb.isEmpty()) {
                sb.append("; ");
            }

            sb.append(fieldError.getDefaultMessage());
        });

        return ErrorResponse.builder()
                .code(HttpStatus.BAD_REQUEST)
                .message(String.format("Ошибка(-и) валидации: %s", sb))
                .build();
    }

    @ExceptionHandler({Exception.class, SendEventException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e, Model model) {
        return ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
    }
}
