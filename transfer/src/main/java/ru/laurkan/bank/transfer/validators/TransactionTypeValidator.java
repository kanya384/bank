package ru.laurkan.bank.transfer.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.laurkan.bank.transfer.model.TransactionType;

import java.util.Arrays;

public class TransactionTypeValidator implements ConstraintValidator<ValidTransactionType, String> {
    @Override
    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(TransactionType.values())
                .map(Enum::toString)
                .map(String::toLowerCase)
                .anyMatch(currency -> currency.equals(input));
    }
}
