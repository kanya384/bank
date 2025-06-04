package ru.laurkan.bank.events.accounts;

import ru.laurkan.bank.events.users.UserInfo;

public record AccountDetailedEvent(
        AccountEvent accountEvent,
        UserInfo userInfo
) {
}
