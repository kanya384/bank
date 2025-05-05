package ru.laurkan.bank.accounts.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {
    @Override
    public boolean isValid(LocalDate birth, ConstraintValidatorContext constraintValidatorContext) {
        var age = ChronoUnit.YEARS.between(birth, LocalDate.now());
        return age >= 18;
    }
}
