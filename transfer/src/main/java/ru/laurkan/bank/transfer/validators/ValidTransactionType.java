package ru.laurkan.bank.transfer.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TransactionTypeValidator.class)
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTransactionType {
    String message() default "Неизвестный тип транзакции";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
