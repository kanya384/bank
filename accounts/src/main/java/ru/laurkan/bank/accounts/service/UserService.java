package ru.laurkan.bank.accounts.service;

import reactor.core.publisher.Mono;
import ru.laurkan.bank.accounts.dto.user.*;

public interface UserService {
    Mono<UserResponseDTO> findByLogin(FindByLoginRequestDTO request);

    Mono<UserResponseDTO> findByAccountId(Long accountId);

    Mono<UserResponseDTO> registerUser(RegisterUserRequestDTO request);

    Mono<UserResponseDTO> update(Long userId, UpdateUserRequestDTO request);

    Mono<UserResponseDTO> changePassword(Long userId, ChangePasswordRequestDTO request);
}
