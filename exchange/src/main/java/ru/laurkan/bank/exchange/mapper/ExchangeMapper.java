package ru.laurkan.bank.exchange.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.laurkan.bank.exchange.dto.ExchangeRateResponseDTO;
import ru.laurkan.bank.exchange.dto.UpdateExchangeRateRequestDTO;
import ru.laurkan.bank.exchange.model.ExchangeRate;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ExchangeMapper {
    ExchangeRateResponseDTO map(ExchangeRate exchangeRate);

    ExchangeRate map(UpdateExchangeRateRequestDTO request);
}
