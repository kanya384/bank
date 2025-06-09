package ru.laurkan.bank.events.users;


public record UserEvent(
        UserEventType eventType,
        Long userId
) {
}
