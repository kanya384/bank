package ru.laurkan.bank.blocker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.blocker.dto.*;
import ru.laurkan.bank.blocker.mapper.TransactionMapper;
import ru.laurkan.bank.blocker.service.BlockerService;

@RestController
@RequiredArgsConstructor
public class BlockerController {
    private final BlockerService blockerService;
    private final TransactionMapper transactionMapper;

    @PostMapping("/deposit")
    public Mono<DecisionResponseDTO> transaction(@RequestBody @Valid DepositTransactionRequestDTO transaction) {
        return blockerService.verify(transactionMapper.map(transaction));
    }

    @PostMapping("/withdrawal")
    public Mono<DecisionResponseDTO> transaction(@RequestBody @Valid WithdrawalTransactionRequestDTO transaction) {
        return blockerService.verify(transactionMapper.map(transaction));
    }

    @PostMapping("/transfer-self")
    public Mono<DecisionResponseDTO> transaction(@RequestBody @Valid SelfTransferTransactionRequestDTO transaction) {
        return blockerService.verify(transactionMapper.map(transaction));
    }

    @PostMapping("/transfer-to-other-user")
    public Mono<DecisionResponseDTO> transaction(@RequestBody @Valid TransferToOtherUserTransactionRequestDTO transaction) {
        return blockerService.verify(transactionMapper.map(transaction));
    }

}
