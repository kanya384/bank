package ru.laurkan.bank.notifications.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.notifications.service.EmailNotificationService;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class SchedulerConfiguration {
    private final EmailNotificationService emailNotificationService;

    @Bean
    public Disposable startScheduler() {
        //TODO - debug
        return Flux.interval(Duration.ofMinutes(1))
                .onBackpressureDrop()
                .flatMap(email -> emailNotificationService.sendMessages()
                        .doOnError(e -> System.out.println(e.getMessage()))
                        .onErrorResume(e -> Mono.empty()))
                .retry()
                .subscribe();
    }
}
