package ru.laurkan.bank.notifications.utils;

import ru.laurkan.bank.events.accounts.AccountDetailedEvent;
import ru.laurkan.bank.events.cash.CashEventDetailed;
import ru.laurkan.bank.events.common.TransactionEventStatus;
import ru.laurkan.bank.events.transfer.TransferEventDetailed;
import ru.laurkan.bank.events.users.UserDetailedEvent;
import ru.laurkan.bank.notifications.model.EmailNotification;

public class MessageBuilders {
    public static EmailNotification buildMessage(AccountDetailedEvent accountDetailedEvent) {
        var subject = switch (accountDetailedEvent.accountEvent().eventType()) {
            case ACCOUNT_CREATED -> "Открыт новый счет!";
        };

        var message = switch (accountDetailedEvent.accountEvent().eventType()) {
            case ACCOUNT_CREATED ->
                    "Новый счет успешно открыт, номер счета: " + accountDetailedEvent.accountEvent().accountId();
        };

        return EmailNotification.builder()
                .recipient(accountDetailedEvent.userInfo().email())
                .subject(subject)
                .message(message)
                .sent(false)
                .build();
    }

    public static EmailNotification buildMessage(TransferEventDetailed transferEvent) {
        var subject = switch (transferEvent.transferEvent().eventType()) {
            case SELF_TRANSFER -> "Результат перевода между своими счетами";
            case TRANSFER_TO_OTHER_USER -> "Результат перевода другому пользователю";
        };

        var message = transactionOperationResultToString(transferEvent.transferEvent().status());

        return EmailNotification.builder()
                .recipient(transferEvent.userInfo().email())
                .subject(subject)
                .message(message)
                .sent(false)
                .build();
    }

    public static EmailNotification buildMessage(CashEventDetailed cashEvent) {
        var subject = switch (cashEvent.cashEvent().eventType()) {
            case DEPOSIT_TRANSACTION -> "Операция внесения денежных средств";
            case WITHDRAWAL_TRANSACTION -> "Операция снятия денежных средств";
        };

        var message = transactionOperationResultToString(cashEvent.cashEvent().status());

        return EmailNotification.builder()
                .recipient(cashEvent.userInfo().email())
                .subject(subject)
                .message(message)
                .sent(false)
                .build();
    }

    public static EmailNotification buildMessage(UserDetailedEvent userDetailedEvent) {
        var subject = switch (userDetailedEvent.userEvent().eventType()) {
            case PASSWORD_CHANGED -> "Смена пароля";
        };

        var message = "Успешная смена пароля, для аккаунта: " + userDetailedEvent.userInfo().login();

        return EmailNotification.builder()
                .recipient(userDetailedEvent.userInfo().email())
                .subject(subject)
                .message(message)
                .sent(false)
                .build();
    }

    private static String transactionOperationResultToString(TransactionEventStatus status) {
        return switch (status) {
            case BLOCKED -> "Операция заблокирована";
            case COMPLETED -> "Операция успешно выполнена";
            case FAILED -> "Ошибка выполнения операции";
            case NOT_ENOUGH_MONEY -> "Недостаточно средств, для выполнения операции";
        };
    }
}
