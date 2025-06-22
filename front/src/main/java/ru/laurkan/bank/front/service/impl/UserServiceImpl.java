package ru.laurkan.bank.front.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.clients.accounts.AccountsClient;
import ru.laurkan.bank.clients.accounts.dto.user.FindByLoginRequest;
import ru.laurkan.bank.front.dto.user.*;
import ru.laurkan.bank.front.mapper.UserMapper;
import ru.laurkan.bank.front.service.UserService;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, ReactiveUserDetailsService {
    private final AccountsClient accountsClient;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(@Value("${clients.accounts.uri}") String accountClientUrl,
                           WebClient webClient,
                           UserMapper userMapper) {
        this.accountsClient = new AccountsClient(accountClientUrl, webClient);
        this.userMapper = userMapper;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return accountsClient
                .findByLogin(FindByLoginRequest.builder()
                        .login(username)
                        .build())
                .map(userMapper::mapToUser);
    }

    @Override
    public Mono<UserResponseDTO> register(RegisterUserRequestDTO request) {
        return accountsClient
                .registerUser(userMapper.map(request))
                .doOnError(e -> {
                    log.error("error register user: {}", e.getMessage());
                })
                .map(userMapper::map);
    }

    @Override
    public Mono<UserResponseDTO> update(Long userId, UpdateUserRequestDTO request) {
        return accountsClient.updateUser(userId, userMapper.map(request))
                .doOnError(e -> {
                    log.error("error update user: {}", e.getMessage());
                })
                .map(userMapper::map);
    }

    @Override
    public Mono<UserResponseDTO> changePassword(Long userId, ChangePasswordRequestDTO request) {
        return accountsClient.changePassword(userId, userMapper.map(request))
                .doOnError(e -> {
                    log.error("error change password of user: {}", e.getMessage());
                })
                .map(userMapper::map);
    }

    @Override
    public Flux<UserAccountResponseDTO> readUsersWithAccounts() {
        return accountsClient.readUsersWithAccounts()
                .map(userMapper::map);
    }
}
