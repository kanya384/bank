package ru.laurkan.bank.accounts.exception;

public class AccountHasMoneyException extends RuntimeException {
    public AccountHasMoneyException() {
        super("На аккаунте не должно быть денег");
    }
}
