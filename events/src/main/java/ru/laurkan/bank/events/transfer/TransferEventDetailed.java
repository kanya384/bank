package ru.laurkan.bank.events.transfer;

import ru.laurkan.bank.events.users.UserInfo;

public record TransferEventDetailed(
        TransferEvent cashEvent,
        UserInfo userInfo
) {
}
