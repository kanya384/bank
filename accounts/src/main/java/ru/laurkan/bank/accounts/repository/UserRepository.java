package ru.laurkan.bank.accounts.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.accounts.model.User;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> findByLogin(String login);

    @Query("select u.id, u.login, u.surname, u.name, u.email from users u left join accounts a on u.id = a.user_id where a.id = :accountId")
    Mono<User> findByAccountId(Long accountId);

    @Query("select u.id, u.login, u.surname, u.name from users u left join accounts a on u.id = a.user_id where a.id is not null")
    Flux<User> findAllUsersWithExistingAccounts();
}
