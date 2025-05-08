package ru.laurkan.bank.cash.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class TransactionTypeValidator implements ConstraintValidator<ValidTransactionType, String> {
    @Override
    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(ru.laurkan.bank.cash.model.TransactionType.values())
                .map(Enum::toString)
                .map(String::toLowerCase)
                .anyMatch(currency -> currency.equals(input));
    }
}
