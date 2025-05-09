package ru.laurkan.bank.cash.mapper;


import org.mapstruct.*;
import ru.laurkan.bank.cash.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.cash.dto.TransactionResponseDTO;
import ru.laurkan.bank.cash.model.DepositTransaction;
import ru.laurkan.bank.cash.model.Transaction;
import ru.laurkan.bank.cash.model.TransactionType;
import ru.laurkan.bank.cash.model.WithdrawalTransaction;
import ru.laurkan.bank.cash.repository.dto.TransactionRepositoryDTO;
import ru.laurkan.bank.clients.blocker.dto.DepositTransactionRequest;
import ru.laurkan.bank.clients.blocker.dto.WithdrawalTransactionRequest;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TransactionMapper {
    public abstract TransactionResponseDTO map(Transaction transaction);

    public abstract Transaction map(TransactionType transactionType, CreateTransactionRequestDTO transaction);

    public abstract Transaction map(TransactionRepositoryDTO transaction);

    public abstract TransactionRepositoryDTO mapToDb(Transaction transaction);

    public abstract DepositTransactionRequest map(DepositTransaction depositTransaction);

    public abstract WithdrawalTransactionRequest map(WithdrawalTransaction depositTransaction);

    @ObjectFactory
    public Transaction createTransactionFromRequest(TransactionType transactionType, CreateTransactionRequestDTO request) {
        return transactionTypeToClass(transactionType);
    }

    @ObjectFactory
    public Transaction createTransactionFromDatabase(TransactionRepositoryDTO transaction) {
        return transactionTypeToClass(transaction.getTransactionType());
    }

    @AfterMapping
    protected void afterMappingResponse(Transaction transaction,
                                        @MappingTarget TransactionResponseDTO transactionResponseDTO) {
        transactionResponseDTO.setTransactionType(transactionClassToTransactionType(transaction));
    }

    @AfterMapping
    protected void afterMappingToRepo(Transaction transaction,
                                      @MappingTarget TransactionRepositoryDTO transactionRepositoryDTO) {
        transactionRepositoryDTO.setTransactionType(transactionClassToTransactionType(transaction));
    }

    protected Transaction transactionTypeToClass(TransactionType transactionType) {
        return switch (transactionType) {
            case DEPOSIT -> new DepositTransaction();
            case WITHDRAWAL -> new WithdrawalTransaction();
        };
    }

    protected TransactionType transactionClassToTransactionType(Transaction transaction) {
        var name = transaction.getClass().getSimpleName()
                .replace("Transaction", "")
                .toUpperCase();

        return TransactionType.valueOf(name);
    }
}
