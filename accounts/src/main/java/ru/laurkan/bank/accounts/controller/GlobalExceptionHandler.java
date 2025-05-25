package ru.laurkan.bank.accounts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import ru.laurkan.bank.accounts.exception.*;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
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

    @ExceptionHandler({NotFoundException.class, UserNotFoundException.class, AccountNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(Exception e, Model model) {
        return ErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({UserAlreadyHasAccountInThisCurrency.class, AccountHasMoneyException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicateException(Exception e, Model model) {
        return ErrorResponse.builder()
                .code(HttpStatus.CONFLICT)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({NotEnoughMoneyException.class})
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public ErrorResponse handleNotEnoughMoneyException(Exception e, Model model) {
        return ErrorResponse.builder()
                .code(HttpStatus.PAYMENT_REQUIRED)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e, Model model) {
        return ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
    }
}
