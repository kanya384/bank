package ru.laurkan.bank.front.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.accounts.exception.RegistrationException;
import ru.laurkan.bank.clients.exception.BadRequestException;

import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({WebExchangeBindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleParameterNotValidException(final WebExchangeBindException e, Model model) {
        List<FieldError> errors = e.getFieldErrors();
        StringBuffer sb = new StringBuffer();
        errors.forEach(fieldError -> {
            if (!sb.isEmpty()) {
                sb.append("; ");
            }

            sb.append(String.format("%s - %s", fieldError.getField(), fieldError.getDefaultMessage()));
        });

        model.addAttribute("error", Map.of(
                "code", HttpStatus.BAD_REQUEST,
                "text", String.format("Ошибка(-и) валидации: %s", sb)
        ));

        return Mono.just("oops");
    }

    @ExceptionHandler({RegistrationException.class, BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<String> handleBNotEnoughMoneyException(Exception e, Model model) {
        model.addAttribute("error", Map.of(
                "code", HttpStatus.BAD_REQUEST,
                "text", e.getMessage()
        ));
        return Mono.just("oops");
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<String> handleException(Exception e, Model model) {
        model.addAttribute("error", Map.of(
                "code", HttpStatus.INTERNAL_SERVER_ERROR,
                "text", e.getMessage()
        ));
        return Mono.just("oops");
    }
}
