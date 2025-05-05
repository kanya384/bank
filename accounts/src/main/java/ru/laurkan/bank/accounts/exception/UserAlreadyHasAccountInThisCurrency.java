package ru.laurkan.bank.accounts.exception;

import ru.laurkan.bank.accounts.model.Currency;

public class UserAlreadyHasAccountInThisCurrency extends RuntimeException {
    public UserAlreadyHasAccountInThisCurrency(Currency currency) {
        super("У пользователя уже есть счет в данной валюте: " + currency.toString());
    }
}
