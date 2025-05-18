package ru.laurkan.bank.cash.repository.dto;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;
import ru.laurkan.bank.cash.model.TransactionStatus;
import ru.laurkan.bank.cash.model.TransactionType;

import java.time.LocalDateTime;

@Getter
@Setter
@Table("transactions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRepositoryDTO {
    @Id
    private Long id;
    private Long accountId;
    private Double amount;
    private TransactionStatus transactionStatus;
    private Boolean notificationSent;
    private TransactionType transactionType;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
