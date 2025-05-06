package ru.laurkan.bank.blocker.service;

import reactor.core.publisher.Mono;
import ru.laurkan.bank.blocker.dto.DecisionResponseDTO;
import ru.laurkan.bank.blocker.model.Transaction;

public interface BlockerService {
    public Mono<DecisionResponseDTO> verify(Transaction transaction);
}
