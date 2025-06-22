package ru.laurkan.bank.cash.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Data
public abstract class Transaction {
    @Id
    private Long id;
    private Long accountId;
    private Double amount;
    private TransactionStatus transactionStatus;
    private Boolean notificationSent;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public Transaction() {
        transactionStatus = TransactionStatus.CREATED;
        notificationSent = false;
    }
}
