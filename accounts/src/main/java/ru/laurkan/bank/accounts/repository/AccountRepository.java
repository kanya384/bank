package ru.laurkan.bank.accounts.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.laurkan.bank.accounts.model.Account;

@Repository
public interface AccountRepository extends R2dbcRepository<Account, Long> {
    Flux<Account> findByUserId(Long userId);
}
