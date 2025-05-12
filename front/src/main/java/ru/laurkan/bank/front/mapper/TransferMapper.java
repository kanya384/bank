package ru.laurkan.bank.front.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.laurkan.bank.clients.transfer.dto.CreateTransactionRequest;
import ru.laurkan.bank.clients.transfer.dto.TransactionResponse;
import ru.laurkan.bank.front.dto.transfer.CreateTransactionRequestDTO;
import ru.laurkan.bank.front.dto.transfer.TransactionResponseDTO;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TransferMapper {
    CreateTransactionRequest map(CreateTransactionRequestDTO request);

    TransactionResponseDTO map(TransactionResponse response);
}
