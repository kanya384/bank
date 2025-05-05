package ru.laurkan.bank.accounts.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.laurkan.bank.accounts.model.Account;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    Flux<Account> findByUserId(Long userId);
}
