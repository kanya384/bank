package ru.laurkan.bank.accounts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.accounts.dto.user.*;
import ru.laurkan.bank.accounts.exception.UserNotFoundException;
import ru.laurkan.bank.accounts.mapper.AccountMapper;
import ru.laurkan.bank.accounts.mapper.UserMapper;
import ru.laurkan.bank.accounts.model.Account;
import ru.laurkan.bank.accounts.model.User;
import ru.laurkan.bank.accounts.repository.AccountRepository;
import ru.laurkan.bank.accounts.repository.UserRepository;
import ru.laurkan.bank.accounts.service.UserService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;


    @Override
    public Mono<UserResponseDTO> findByLogin(FindByLoginRequestDTO request) {
        return userRepository.findByLogin(request.getLogin())
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(userMapper::map);
    }

    @Override
    public Mono<UserResponseDTO> findByAccountId(Long accountId) {
        return userRepository.findByAccountId(accountId)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(userMapper::map);
    }

    @Override
    public Mono<UserResponseDTO> registerUser(RegisterUserRequestDTO request) {
        User user = userMapper.map(request);
        return userRepository.save(user)
                .map(userMapper::map);
    }

    @Override
    public Mono<UserResponseDTO> update(Long userId, UpdateUserRequestDTO request) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .map(user -> userMapper.update(request, user))
                .flatMap(userRepository::save)
                .map(userMapper::map);
    }

    @Override
    public Mono<UserResponseDTO> changePassword(Long userId, ChangePasswordRequestDTO request) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .doOnNext(user -> user.setPassword(passwordEncoder.encode(request.getPassword())))
                .flatMap(userRepository::save)
                .map(userMapper::map);
    }

    @Override
    public Flux<UsersAccountsResponseDTO> findAllUsersWithAccounts() {
        return userRepository.findAll()
                .map(userMapper::mapUserAccounts)
                .collectList()
                .zipWith(accountRepository.findAll()
                        .collectList()
                )
                .flatMapMany(tuple -> {
                    var users = tuple.getT1();
                    var accounts = tuple.getT2();

                    return Flux.fromIterable(mergeUsersAndAccounts(users, accounts));
                })
                .filter(user -> user.getAccounts() != null);
    }

    private Collection<UsersAccountsResponseDTO> mergeUsersAndAccounts(List<UsersAccountsResponseDTO> users,
                                                                       List<Account> accounts) {
        Map<Long, UsersAccountsResponseDTO> usersMap = new HashMap<>();
        users.forEach(user -> {
            usersMap.put(user.getId(), user);
        });

        accounts.forEach(account -> {
            var user = usersMap.get(account.getUserId());
            if (user.getAccounts() == null) {
                user.setAccounts(new ArrayList<>());
            }
            user.getAccounts().add(accountMapper.map(account));
        });

        return usersMap.values();
    }
}
