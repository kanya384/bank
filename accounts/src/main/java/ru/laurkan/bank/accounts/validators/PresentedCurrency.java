package ru.laurkan.bank.accounts.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CurrencyValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PresentedCurrency {
    String message() default "Неизвестная валюта";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
