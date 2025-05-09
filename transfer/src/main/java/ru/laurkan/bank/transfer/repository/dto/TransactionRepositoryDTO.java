package ru.laurkan.bank.transfer.repository.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;
import ru.laurkan.bank.transfer.model.TransactionStatus;
import ru.laurkan.bank.transfer.model.TransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
@Table("transactions")
public class TransactionRepositoryDTO {
    @Id
    private Long id;
    private Long accountId;
    private Long receiverAccountId;
    private Double amount;
    private TransactionStatus transactionStatus;
    private Boolean notificationSent;
    private TransactionType transactionType;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
