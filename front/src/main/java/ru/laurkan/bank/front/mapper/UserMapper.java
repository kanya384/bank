package ru.laurkan.bank.front.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.laurkan.bank.front.client.accounts.dto.user.RegisterUserRequestDTO;
import ru.laurkan.bank.front.client.accounts.dto.user.UserResponseDTO;
import ru.laurkan.bank.front.model.User;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    public abstract User map(UserResponseDTO user);

    public abstract User map(RegisterUserRequestDTO userRequestDTO);
}
