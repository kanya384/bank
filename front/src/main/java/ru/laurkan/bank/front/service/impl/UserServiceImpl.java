package ru.laurkan.bank.front.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.client.accounts.AccountsClient;
import ru.laurkan.bank.front.client.accounts.dto.user.*;
import ru.laurkan.bank.front.mapper.UserMapper;
import ru.laurkan.bank.front.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, ReactiveUserDetailsService {
    private final AccountsClient accountsClient;
    private final UserMapper userMapper;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accountsClient
                .findByLogin(FindByLoginRequestDTO.builder()
                        .login(username)
                        .build())
                .map(userMapper::map);
    }

    @Override
    public Mono<UserResponseDTO> register(RegisterUserRequestDTO request) {
        return accountsClient
                .registerUser(request);
    }

    @Override
    public Mono<UserResponseDTO> update(Long userId, UpdateUserRequestDTO request) {
        return accountsClient.updateUser(userId, request);
    }

    @Override
    public Mono<UserResponseDTO> changePassword(Long userId, ChangePasswordRequestDTO request) {
        return accountsClient.changePassword(userId, request);
    }
}
