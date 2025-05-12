package ru.laurkan.bank.front.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.laurkan.bank.clients.cash.dto.CreateTransactionRequest;
import ru.laurkan.bank.clients.cash.dto.TransactionResponse;
import ru.laurkan.bank.front.dto.cash.CashTransactionResponseDTO;
import ru.laurkan.bank.front.dto.cash.CreateCashTransactionRequestDTO;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CashMapper {
    CreateTransactionRequest map(CreateCashTransactionRequestDTO request);

    CashTransactionResponseDTO map(TransactionResponse response);
}
