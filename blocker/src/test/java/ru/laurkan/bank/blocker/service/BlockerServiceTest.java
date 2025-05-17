package ru.laurkan.bank.blocker.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import ru.laurkan.bank.blocker.model.DepositTransaction;
import ru.laurkan.bank.blocker.service.impl.BlockerServiceImpl;

import java.time.LocalDateTime;

@WebFluxTest({BlockerServiceImpl.class, BlockerService.class})
@ActiveProfiles("test")
public class BlockerServiceTest {
    @Autowired
    private BlockerService blockerService;

    @Test
    public void verify_shouldVerifyTransaction() {
        var transaction = new DepositTransaction();
        transaction.setAccountId(1L);
        transaction.setMoney(100.0);
        transaction.setCreatedAt(LocalDateTime.now());

        StepVerifier.create(blockerService.verify(transaction))
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }
}
