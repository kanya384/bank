package ru.laurkan.bank.transfer.mapper;

import org.springframework.stereotype.Component;
import ru.laurkan.bank.clients.notification.dto.CreateEmailNotificationRequest;
import ru.laurkan.bank.transfer.model.SelfTransferTransaction;
import ru.laurkan.bank.transfer.model.Transaction;
import ru.laurkan.bank.transfer.model.TransferToOtherUserTransaction;

@Component
public class NotificationMapper {
    public CreateEmailNotificationRequest map(Transaction transaction, String email, String recipientName) {
        String subject = switch (transaction) {
            case SelfTransferTransaction selfTransferTransaction ->
                    String.format("Результат выполнения перевода со счета %d на счет %d",
                            selfTransferTransaction.getAccountId(),
                            selfTransferTransaction.getReceiverAccountId());
            case TransferToOtherUserTransaction transferToOtherUserTransaction ->
                    String.format("Результат выполнения перевода со счета %d клиенту %s на счет %d",
                            transferToOtherUserTransaction.getAccountId(),
                            recipientName,
                            transferToOtherUserTransaction.getReceiverAccountId());
            default -> throw new RuntimeException("Неизвестный тип транзакци");
        };

        CreateEmailNotificationRequest request = CreateEmailNotificationRequest
                .builder()
                .recipient(email)
                .subject(subject)
                .build();

        switch (transaction.getTransactionStatus()) {
            case BLOCKED -> request.setMessage("Операция заблокирована");
            case FAILED -> request.setMessage("Ошибка совершения операции! Пожалуйста попробуйте позже...");
            case COMPLETED -> request.setMessage("Операция выполнена успешно");
            case NOT_ENOUGH_MONEY -> request.setMessage("Недостаточно денег для совершения операции");
        }

        return request;
    }
}
