package ru.laurkan.bank.front.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.laurkan.bank.clients.exchange.dto.ExchangeRateResponse;
import ru.laurkan.bank.front.dto.exchange.ExchangeRateResponseDTO;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ExchangeMapper {
    ExchangeRateResponseDTO map(ExchangeRateResponse response);
}
