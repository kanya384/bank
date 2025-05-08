package ru.laurkan.bank.cash.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.laurkan.bank.cash.model.TransactionStatus;
import ru.laurkan.bank.cash.repository.dto.TransactionRepositoryDTO;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<TransactionRepositoryDTO, Long> {
    Flux<TransactionRepositoryDTO> findByTransactionStatus(TransactionStatus transactionStatus);
}
