package ru.laurkan.bank.front.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.laurkan.bank.clients.accounts.dto.accounts.UsersAccountsResponse;
import ru.laurkan.bank.clients.accounts.dto.user.ChangePasswordRequest;
import ru.laurkan.bank.clients.accounts.dto.user.RegisterUserRequest;
import ru.laurkan.bank.clients.accounts.dto.user.UpdateUserRequest;
import ru.laurkan.bank.clients.accounts.dto.user.UserResponse;
import ru.laurkan.bank.front.dto.user.*;
import ru.laurkan.bank.front.model.User;

@Mapper(
        uses = {AccountMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    public abstract User mapToUser(UserResponse user);

    public abstract UpdateUserRequest map(UpdateUserRequestDTO request);

    public abstract UserAccountResponseDTO map(UsersAccountsResponse request);

    public abstract UserResponseDTO map(UserResponse user);

    public abstract User map(UserResponseDTO user);

    public abstract RegisterUserRequest map(RegisterUserRequestDTO userRequestDTO);

    public abstract ChangePasswordRequest map(ChangePasswordRequestDTO userRequestDTO);
}
