package ru.laurkan.bank.front.service;

import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.dto.user.ChangePasswordRequestDTO;
import ru.laurkan.bank.front.dto.user.RegisterUserRequestDTO;
import ru.laurkan.bank.front.dto.user.UpdateUserRequestDTO;
import ru.laurkan.bank.front.dto.user.UserResponseDTO;


public interface UserService {
    Mono<UserResponseDTO> register(RegisterUserRequestDTO request);

    Mono<UserResponseDTO> update(Long userId, UpdateUserRequestDTO request);

    Mono<UserResponseDTO> changePassword(Long userId, ChangePasswordRequestDTO request);
}
