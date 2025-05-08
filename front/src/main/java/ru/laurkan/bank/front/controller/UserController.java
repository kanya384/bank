package ru.laurkan.bank.front.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.dto.user.ChangePasswordRequestDTO;
import ru.laurkan.bank.front.dto.user.RegisterUserRequestDTO;
import ru.laurkan.bank.front.dto.user.UpdateUserRequestDTO;
import ru.laurkan.bank.front.mapper.UserMapper;
import ru.laurkan.bank.front.model.User;
import ru.laurkan.bank.front.service.UserService;
import ru.laurkan.bank.front.utils.SecurityUtils;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/profile")
    public Mono<String> profile(Model model) {
        model.addAttribute("user", ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .switchIfEmpty(Mono.empty())
                .map(p -> (User) p.getPrincipal()));

        return Mono.just("profile");
    }

    @GetMapping("/register")
    public Mono<String> register() {
        return Mono.just("register");
    }

    @PostMapping("/register")
    public Mono<String> registerAndAuthenticate(@ModelAttribute @Valid RegisterUserRequestDTO requestDTO, ServerWebExchange exchange) {
        return userService
                .register(requestDTO)
                .map(userMapper::map)
                .flatMap(userResponseDTO -> SecurityUtils.updateUserInSession(userResponseDTO, exchange)
                        .then(Mono.just("redirect:/")));
    }

    @PostMapping("/user/{id}/update")
    public Mono<String> updateUser(@PathVariable Long id, @ModelAttribute @Valid UpdateUserRequestDTO request,
                                   ServerWebExchange exchange) {
        return userService
                .update(id, request)
                .map(userMapper::map)
                .flatMap(userResponseDTO -> SecurityUtils.updateUserInSession(userResponseDTO, exchange)
                        .then(Mono.just("redirect:/")));
    }

    @PostMapping("/user/{id}/change-password")
    public Mono<String> changePassword(@PathVariable Long id,
                                       @ModelAttribute @Valid ChangePasswordRequestDTO request,
                                       ServerWebExchange exchange) {
        return userService
                .changePassword(id, request)
                .map(userMapper::map)
                .then(Mono.just("redirect:/"));
    }
}
