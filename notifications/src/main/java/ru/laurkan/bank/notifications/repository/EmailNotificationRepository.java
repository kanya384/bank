package ru.laurkan.bank.notifications.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.laurkan.bank.notifications.model.EmailNotification;

@Repository
public interface EmailNotificationRepository extends ReactiveCrudRepository<EmailNotification, Long> {
    Flux<EmailNotification> findBySent(boolean sent);
}
