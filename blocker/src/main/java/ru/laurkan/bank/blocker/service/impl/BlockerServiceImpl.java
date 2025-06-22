package ru.laurkan.bank.blocker.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.blocker.dto.DecisionResponseDTO;
import ru.laurkan.bank.blocker.model.Transaction;
import ru.laurkan.bank.blocker.model.TransactionValidator;
import ru.laurkan.bank.blocker.service.BlockerService;

@Log4j2
@Service
public class BlockerServiceImpl implements BlockerService {
    @Override
    public Mono<DecisionResponseDTO> verify(Transaction transaction) {
        log.debug("received transaction verification request: {}", transaction);
        return TransactionValidator.validate(transaction)
                .map(decision -> DecisionResponseDTO.builder()
                        .isBlocked(!decision)
                        .build())
                .doOnNext(decisionResponseDTO -> {
                    log.info("transaction ({}) verified, decision: {}", transaction, decisionResponseDTO);
                });
    }
}
