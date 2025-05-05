package ru.laurkan.bank.accounts.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super(String.format("Пользователь с id = %d не найден", userId));
    }

    public UserNotFoundException() {
        super("Пользователь с таким логином и паролем не найден");
    }
}
