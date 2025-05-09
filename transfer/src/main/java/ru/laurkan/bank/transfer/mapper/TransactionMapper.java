package ru.laurkan.bank.transfer.mapper;


import org.mapstruct.*;
import ru.laurkan.bank.clients.blocker.dto.SelfTransferTransactionRequest;
import ru.laurkan.bank.clients.blocker.dto.TransferToOtherUserTransactionRequest;
import ru.laurkan.bank.transfer.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.transfer.dto.TransactionResponseDTO;
import ru.laurkan.bank.transfer.exception.UnknownTransactionTypeException;
import ru.laurkan.bank.transfer.model.SelfTransferTransaction;
import ru.laurkan.bank.transfer.model.Transaction;
import ru.laurkan.bank.transfer.model.TransactionType;
import ru.laurkan.bank.transfer.model.TransferToOtherUserTransaction;
import ru.laurkan.bank.transfer.repository.dto.TransactionRepositoryDTO;

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

    public abstract SelfTransferTransactionRequest map(SelfTransferTransaction transaction);

    public abstract TransferToOtherUserTransactionRequest map(TransferToOtherUserTransaction transaction);

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
            case SELF_TRANSFER -> new SelfTransferTransaction();
            case TRANSFER_TO_OTHER_USER -> new TransferToOtherUserTransaction();
        };
    }

    protected TransactionType transactionClassToTransactionType(Transaction transaction) {
        var name = transaction.getClass().getSimpleName()
                .replace("Transaction", "")
                .toUpperCase();

        return switch (name) {
            case "SELFTRANSFER" -> TransactionType.SELF_TRANSFER;
            case "TRANSFERTOOTHERUSER" -> TransactionType.TRANSFER_TO_OTHER_USER;
            default -> throw new UnknownTransactionTypeException("неизвестный тип транзакции " + name);
        };
    }
}
