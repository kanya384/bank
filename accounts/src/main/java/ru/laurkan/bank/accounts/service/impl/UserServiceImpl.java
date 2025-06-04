package ru.laurkan.bank.accounts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.accounts.dto.user.*;
import ru.laurkan.bank.accounts.exception.SendEventException;
import ru.laurkan.bank.accounts.exception.UserNotFoundException;
import ru.laurkan.bank.accounts.mapper.UserMapper;
import ru.laurkan.bank.accounts.model.User;
import ru.laurkan.bank.accounts.repository.UserRepository;
import ru.laurkan.bank.accounts.service.UserService;
import ru.laurkan.bank.events.users.UserEvent;
import ru.laurkan.bank.events.users.UserEventType;
import ru.laurkan.bank.events.users.UserInfo;

import static ru.laurkan.bank.accounts.configuration.KafkaConfiguration.OUTPUT_USER_EVENTS_TOPIC;
import static ru.laurkan.bank.accounts.configuration.KafkaConfiguration.USER_NOTIFICATION_EVENTS_TOPIC;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final KafkaTemplate<Long, UserInfo> domainEvents;
    private final KafkaTemplate<Long, UserEvent> notifications;


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
                .doOnSuccess(userSaved -> {
                    try {
                        domainEvents.send(OUTPUT_USER_EVENTS_TOPIC, userSaved.getId(), userMapper.event(userSaved))
                                .get();
                    } catch (Exception e) {
                        throw new SendEventException(e.getMessage());
                    }
                })
                .map(userMapper::map);
    }

    @Override
    public Mono<UserResponseDTO> update(Long userId, UpdateUserRequestDTO request) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .map(user -> userMapper.update(request, user))
                .flatMap(userRepository::save)
                .doOnSuccess(userSaved -> {
                    try {
                        domainEvents.send(OUTPUT_USER_EVENTS_TOPIC, userSaved.getId(), userMapper.event(userSaved))
                                .get();
                    } catch (Exception e) {
                        throw new SendEventException(e.getMessage());
                    }
                })
                .map(userMapper::map);
    }

    @Transactional
    @Override
    public Mono<UserResponseDTO> changePassword(Long userId, ChangePasswordRequestDTO request) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException(userId)))
                .doOnNext(user -> user.setPassword(passwordEncoder.encode(request.getPassword())))
                .flatMap(userRepository::save)
                .doOnSuccess(user -> {
                            try {
                                notifications.send(USER_NOTIFICATION_EVENTS_TOPIC, user.getId(),
                                        new UserEvent(UserEventType.PASSWORD_CHANGED, user.getId())).get();
                            } catch (Exception e) {
                                throw new SendEventException(e.getMessage());
                            }
                        }
                )
                .map(userMapper::map);
    }

    @Override
    public Flux<UsersAccountsResponseDTO> findAllUsersWithAccounts() {
        return userRepository.findAllUsersWithExistingAccounts()
                .map(userMapper::mapUserAccounts);
    }
}
