package ru.laurkan.bank.front.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.front.model.User;

import java.util.List;

import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;

@Component
public class SecurityUtils {
    public static Mono<Long> getUserId() {
        return ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .switchIfEmpty(Mono.empty())
                .map(p -> (User) p.getPrincipal())
                .map(User::getId);
    }

    public static Mono<Void> updateUserInSession(User user, ServerWebExchange exchange) {
        return exchange.getSession()
                .doOnNext(session -> {
                    SecurityContextImpl securityContext
                            = new SecurityContextImpl();
                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(user,
                            null, List.of());
                    securityContext.setAuthentication(authentication);

                    session.getAttributes()
                            .put(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME,
                                    securityContext);
                })
                .flatMap(WebSession::changeSessionId);
    }
}
