package ru.laurkan.bank.accounts.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AdultValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Adult {
    String message() default "Пользователь должен быть совершеннолетним";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
