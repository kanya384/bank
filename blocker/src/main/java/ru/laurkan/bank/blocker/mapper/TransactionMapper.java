package ru.laurkan.bank.blocker.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.laurkan.bank.blocker.dto.DepositTransactionRequestDTO;
import ru.laurkan.bank.blocker.dto.SelfTransferTransactionRequestDTO;
import ru.laurkan.bank.blocker.dto.TransferToOtherUserTransactionRequestDTO;
import ru.laurkan.bank.blocker.dto.WithdrawalTransactionRequestDTO;
import ru.laurkan.bank.blocker.model.DepositTransaction;
import ru.laurkan.bank.blocker.model.SelfTransferTransaction;
import ru.laurkan.bank.blocker.model.TransferToOtherUserTransaction;
import ru.laurkan.bank.blocker.model.WithdrawalTransaction;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TransactionMapper {
    DepositTransaction map(DepositTransactionRequestDTO transaction);

    WithdrawalTransaction map(WithdrawalTransactionRequestDTO transaction);

    SelfTransferTransaction map(SelfTransferTransactionRequestDTO transaction);

    TransferToOtherUserTransaction map(TransferToOtherUserTransactionRequestDTO transaction);
}
