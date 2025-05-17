package ru.laurkan.bank.accounts.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.laurkan.bank.accounts.SecurityTestConfiguration;
import ru.laurkan.bank.accounts.configuration.PasswordEncoderConfiguration;
import ru.laurkan.bank.accounts.dto.user.*;
import ru.laurkan.bank.accounts.exception.UserNotFoundException;
import ru.laurkan.bank.accounts.mapper.UserMapperImpl;
import ru.laurkan.bank.accounts.model.User;
import ru.laurkan.bank.accounts.repository.UserRepository;
import ru.laurkan.bank.accounts.service.impl.UserServiceImpl;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@WebFluxTest({UserServiceImpl.class, UserMapperImpl.class})
@ActiveProfiles("test")
@Import({SecurityTestConfiguration.class, PasswordEncoderConfiguration.class})
public class UserServiceTest {
    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        reset(userRepository);
    }

    @Test
    public void findByLogin_shouldReturnUserByLogin() {
        var request = new FindByLoginRequestDTO();
        request.setLogin("test");

        when(userRepository.findByLogin(request.getLogin()))
                .thenReturn(Mono.just(User.builder()
                        .id(1L)
                        .login("login")
                        .name("name")
                        .surname("surname")
                        .email("email")
                        .birth(LocalDate.of(1990, 6, 20))
                        .build()));

        StepVerifier.create(userService.findByLogin(request))
                .assertNext(userResponseDTO -> Assertions.assertThat(userResponseDTO)
                        .withFailMessage("Не пустой")
                        .isNotNull()
                        .extracting(UserResponseDTO::getId)
                        .withFailMessage("Id равен 1")
                        .isEqualTo(1L)
                );
    }

    @Test
    public void findByLogin_shouldReturnExceptionIfNotFound() {
        var request = new FindByLoginRequestDTO();
        request.setLogin("test");

        when(userRepository.findByLogin(request.getLogin()))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.findByLogin(request))
                .expectErrorMatches(exception -> exception instanceof UserNotFoundException)
                .verify();
    }

    @Test
    public void findByAccountId_shouldReturnUserByAccountId() {
        when(userRepository.findByAccountId(1L))
                .thenReturn(Mono.just(User.builder()
                        .id(1L)
                        .login("login")
                        .name("name")
                        .surname("surname")
                        .email("email")
                        .birth(LocalDate.of(1990, 6, 20))
                        .build()));

        StepVerifier.create(userService.findByAccountId(1L))
                .assertNext(userResponseDTO -> Assertions.assertThat(userResponseDTO)
                        .withFailMessage("Не пустой")
                        .isNotNull()
                        .extracting(UserResponseDTO::getId)
                        .withFailMessage("Id равен 1")
                        .isEqualTo(1L)
                );
    }

    @Test
    public void findByAccountId_shouldReturnExceptionIfNotFound() {
        when(userRepository.findByAccountId(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.findByAccountId(1L))
                .expectErrorMatches(exception -> exception instanceof UserNotFoundException)
                .verify();
    }

    @Test
    public void registerUser_shouldRegisterUser() {
        var request = RegisterUserRequestDTO.builder()
                .login("test")
                .password("password")
                .name("name")
                .surname("surname")
                .email("test01@mail.ru")
                .birth(LocalDate.now())
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.just(User.builder()
                        .id(1L)
                        .build()));

        StepVerifier.create(userService.registerUser(request))
                .assertNext(userResponseDTO -> Assertions.assertThat(userResponseDTO)
                        .withFailMessage("Не нулевой ответ")
                        .isNotNull()
                        .extracting(UserResponseDTO::getId)
                        .withFailMessage("Id равен 1")
                        .isEqualTo(1L)
                )
                .verifyComplete();
    }

    @Test
    public void update_shouldUpdateUser() {
        var request = UpdateUserRequestDTO.builder()
                .name("new-name")
                .surname("surname")
                .email("test01@mail.ru")
                .birth(LocalDate.now())
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Mono.just(User.builder()
                        .id(1L)
                        .name("name")
                        .surname("surname")
                        .email("test01@mail.ru")
                        .birth(LocalDate.now())
                        .build()));

        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.just(User.builder()
                        .id(1L)
                        .name("new-name")
                        .build()));

        StepVerifier.create(userService.update(1L, request))
                .assertNext(userResponseDTO -> Assertions.assertThat(userResponseDTO)
                        .withFailMessage("Не пустой ответ")
                        .isNotNull()
                        .extracting(UserResponseDTO::getName)
                        .withFailMessage("Имя пользователя обновилось")
                        .isEqualTo("new-name")
                )
                .verifyComplete();
    }

    @Test
    public void update_shouldReturnExceptionIfUserNotFound() {
        var request = UpdateUserRequestDTO.builder()
                .name("new-name")
                .surname("surname")
                .email("test01@mail.ru")
                .birth(LocalDate.now())
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.update(1L, request))
                .expectErrorMatches(exception -> exception instanceof UserNotFoundException
                )
                .verify();
    }

    @Test
    public void changePassword_shouldUpdatePassword() {
        var request = new ChangePasswordRequestDTO();
        request.setPassword("new-password");

        var encryptedPass = passwordEncoder.encode(request.getPassword());

        when(userRepository.findById(1L))
                .thenReturn(Mono.just(User.builder()
                        .id(1L)
                        .name("name")
                        .surname("surname")
                        .email("test01@mail.ru")
                        .birth(LocalDate.now())
                        .build()));

        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.just(User.builder()
                        .id(1L)
                        .password(encryptedPass)
                        .build()));

        StepVerifier.create(userService.changePassword(1L, request))
                .assertNext(userResponseDTO -> assertTrue(passwordEncoder
                        .matches(request.getPassword(), userResponseDTO.getPassword()))
                )
                .verifyComplete();
    }

    @Test
    public void findAllUsersWithAccounts_shouldReturnUsersWithAccounts() {
        when(userRepository.findAllUsersWithExistingAccounts())
                .thenReturn(Flux.just(User.builder()
                        .id(1L)
                        .name("name")
                        .surname("surname")
                        .email("test01@mail.ru")
                        .birth(LocalDate.now())
                        .build()));

        StepVerifier.create(userService.findAllUsersWithAccounts()
                        .collectList())
                .assertNext(usersAccountsResponseDTOList -> Assertions.assertThat(usersAccountsResponseDTOList)
                        .hasSize(1)
                )
                .verifyComplete();
    }
}
