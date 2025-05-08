package ru.laurkan.bank.front.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.laurkan.bank.clients.accounts.dto.accounts.AccountResponse;
import ru.laurkan.bank.clients.accounts.dto.accounts.CreateAccountRequest;
import ru.laurkan.bank.front.dto.account.AccountResponseDTO;
import ru.laurkan.bank.front.dto.account.CreateAccountRequestDTO;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {
    CreateAccountRequest map(CreateAccountRequestDTO request);

    AccountResponseDTO map(AccountResponse accountResponse);
}
