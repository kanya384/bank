package ru.laurkan.bank.blocker.service.impl;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.blocker.dto.DecisionResponseDTO;
import ru.laurkan.bank.blocker.model.Transaction;
import ru.laurkan.bank.blocker.model.TransactionValidator;
import ru.laurkan.bank.blocker.service.BlockerService;

@Service
public class BlockerServiceImpl implements BlockerService {
    @Override
    public Mono<DecisionResponseDTO> verify(Transaction transaction) {
        return TransactionValidator.validate(transaction)
                .map(decision -> DecisionResponseDTO.builder()
                        .isBlocked(!decision)
                        .build());
    }
}
