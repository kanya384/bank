package ru.laurkan.bank.events.users;

import java.time.LocalDate;

public record UserInfo(
        Long id,
        String login,
        String surname,
        String name,
        String email,
        LocalDate birth
) {
}
