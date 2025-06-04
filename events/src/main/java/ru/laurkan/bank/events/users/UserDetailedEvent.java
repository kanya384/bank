package ru.laurkan.bank.events.users;

public record UserDetailedEvent(
        UserEvent userEvent,
        UserInfo userInfo
) {
}
