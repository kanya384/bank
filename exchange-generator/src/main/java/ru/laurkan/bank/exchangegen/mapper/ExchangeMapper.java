package ru.laurkan.bank.exchangegen.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.laurkan.bank.clients.exchange.dto.UpdateExchangeRateRequest;
import ru.laurkan.bank.exchangegen.model.ExchangeRate;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ExchangeMapper {

    UpdateExchangeRateRequest map(ExchangeRate exchangeRate);
}
