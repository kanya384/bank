package ru.laurkan.bank.front.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.dto.user.*;


public interface UserService {
    Mono<UserResponseDTO> register(RegisterUserRequestDTO request);

    Mono<UserResponseDTO> update(Long userId, UpdateUserRequestDTO request);

    Mono<UserResponseDTO> changePassword(Long userId, ChangePasswordRequestDTO request);

    Flux<UserAccountResponseDTO> readUsersWithAccounts();
}
