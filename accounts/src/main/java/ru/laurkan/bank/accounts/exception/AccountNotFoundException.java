package ru.laurkan.bank.accounts.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long accountId) {
        super(String.format("Аккаунт с id = %d не найден", accountId));
    }
}
