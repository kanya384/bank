package ru.laurkan.bank.accounts.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.laurkan.bank.accounts.dto.user.RegisterUserRequestDTO;
import ru.laurkan.bank.accounts.dto.user.UpdateUserRequestDTO;
import ru.laurkan.bank.accounts.dto.user.UserResponseDTO;
import ru.laurkan.bank.accounts.model.User;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User map(RegisterUserRequestDTO data) {
        return User.builder()
                .login(data.getLogin())
                .password(passwordEncoder.encode(data.getPassword()))
                .surname(data.getSurname())
                .name(data.getName())
                .email(data.getEmail())
                .birth(data.getBirth())
                .build();
    }

    public abstract UserResponseDTO map(User user);

    public abstract User update(UpdateUserRequestDTO data, @MappingTarget User user);
}
