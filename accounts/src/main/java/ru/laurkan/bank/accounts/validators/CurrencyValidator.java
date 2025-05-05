package ru.laurkan.bank.accounts.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.laurkan.bank.accounts.model.Currency;

import java.util.Arrays;

public class CurrencyValidator implements ConstraintValidator<PresentedCurrency, String> {
    @Override
    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(Currency.values())
                .map(Enum::toString)
                .anyMatch(currency -> currency.equals(input));
    }
}
