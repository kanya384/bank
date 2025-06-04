package ru.laurkan.bank.events.cash;

import ru.laurkan.bank.events.users.UserInfo;

public record CashEventDetailed(
        CashEvent cashEvent,
        UserInfo userInfo
) {
}
