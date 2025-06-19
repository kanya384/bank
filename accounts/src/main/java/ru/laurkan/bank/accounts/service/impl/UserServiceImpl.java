package ru.laurkan.bank.accounts.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

@Log4j2
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
                .switchIfEmpty(Mono.error(() -> {
                    log.debug("user not found by login [{}]", request.getLogin());
                    return new UserNotFoundException();
                }))
                .map(userMapper::map);
    }

    @Override
    public Mono<UserResponseDTO> findByAccountId(Long accountId) {
        return userRepository.findByAccountId(accountId)
                .switchIfEmpty(Mono.error(() -> {
                    log.debug("user not found by account [{}]", accountId);
                    return new UserNotFoundException();
                }))
                .map(userMapper::map);
    }

    @Override
    public Mono<UserResponseDTO> registerUser(RegisterUserRequestDTO request) {
        User user = userMapper.map(request);
        log.debug("user registration request received: {}", request);
        return userRepository.save(user)
                .doOnError(e -> {
                    log.error("store user to db exception {}", e.getMessage());
                })
                .doOnSuccess(this::sendDomainEventToKafka)
                .map(userMapper::map)
                .doOnNext(userResponseDTO -> {
                    log.info("user successfully registered id = {}, login = {}", userResponseDTO.getId(), userResponseDTO.getLogin());
                });
    }

    @Override
    public Mono<UserResponseDTO> update(Long userId, UpdateUserRequestDTO request) {
        log.debug("user update request received: {}", request);
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(() -> {
                    log.error("user not found by id = {}", userId);
                    return new UserNotFoundException();
                }))
                .map(user -> userMapper.update(request, user))
                .flatMap(userRepository::save)
                .doOnError(e -> {
                    log.error("update user in db exception {}", e.getMessage());
                })
                .doOnSuccess(this::sendDomainEventToKafka)
                .map(userMapper::map)
                .doOnNext(userResponseDTO -> {
                    log.info("user successfully updated id = {}, login = {}", userResponseDTO.getId(), userResponseDTO.getLogin());
                });
    }

    @Transactional
    @Override
    public Mono<UserResponseDTO> changePassword(Long userId, ChangePasswordRequestDTO request) {
        log.debug("user changePassword request received: {}", request);
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(() -> {
                    log.error("user not found (id = {})", userId);
                    return new UserNotFoundException(userId);
                }))
                .doOnNext(user -> user.setPassword(passwordEncoder.encode(request.getPassword())))
                .flatMap(userRepository::save)
                .doOnError(e -> {
                    log.error("update users password in db exception {}", e.getMessage());
                })
                .doOnSuccess(user -> {
                        Mono.fromFuture(notifications.send(USER_NOTIFICATION_EVENTS_TOPIC, user.getId(),
                                    new UserEvent(UserEventType.PASSWORD_CHANGED, user.getId())))
                            .doOnError(e -> {
                                log.error("error sending notification to kafka {}", e.getMessage());
                                throw new SendEventException(e.getMessage());
                            })
                            .doOnSuccess(result -> {
                                log.debug("notification sent to kafka, offset - {}",
                                        result.getRecordMetadata().offset());
                            })
                            .subscribe();
                    }
                )
                .map(userMapper::map)
                .doOnNext(userResponseDTO -> {
                    log.info("user successfully changed password id = {}, login = {}", userResponseDTO.getId(),
                            userResponseDTO.getLogin());
                });
    }

    @Override
    public Flux<UsersAccountsResponseDTO> findAllUsersWithAccounts() {
        return userRepository.findAllUsersWithExistingAccounts()
                .map(userMapper::mapUserAccounts);
    }

    private void sendDomainEventToKafka(User user) {
        Mono.fromFuture(domainEvents.send(OUTPUT_USER_EVENTS_TOPIC, user.getId(), userMapper.event(user)))
                .doOnError(e -> {
                    log.error("error sending event to kafka {}", e.getMessage());
                    throw new SendEventException(e.getMessage());
                })
                .doOnSuccess(result -> {
                    log.debug("event sent to kafka, offset - {}",
                            result.getRecordMetadata().offset());
                })
                .subscribe();
    }
}
