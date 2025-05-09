package ru.laurkan.bank.accounts.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.accounts.dto.user.*;
import ru.laurkan.bank.accounts.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/find-by-login")
    public Mono<UserResponseDTO> findByLogin(@RequestBody @Valid FindByLoginRequestDTO request) {
        return userService.findByLogin(request);
    }

    @GetMapping(value = "/find-by-account-id/{account-id}")
    public Mono<UserResponseDTO> findByAccountId(@PathVariable("account-id") Long accountId) {
        return userService.findByAccountId(accountId);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDTO> registerUser(@RequestBody @Valid RegisterUserRequestDTO request) {
        return userService.registerUser(request);
    }

    @PutMapping("/{id}")
    public Mono<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserRequestDTO requestDTO) {
        return userService.update(id, requestDTO);
    }

    @PutMapping("/change-password/{id}")
    public Mono<UserResponseDTO> changePassword(@PathVariable Long id, @RequestBody @Valid ChangePasswordRequestDTO requestDTO) {
        return userService.changePassword(id, requestDTO);
    }
}
