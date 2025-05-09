package ru.laurkan.bank.accounts.exception;

public class NotEnoughMoneyException extends RuntimeException {
    public NotEnoughMoneyException() {
        super("На аккаунте недостаточно денег");
    }
}
