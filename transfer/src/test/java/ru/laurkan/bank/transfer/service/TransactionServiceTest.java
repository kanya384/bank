package ru.laurkan.bank.transfer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.laurkan.bank.transfer.dto.CreateTransactionRequestDTO;
import ru.laurkan.bank.transfer.dto.TransactionResponseDTO;
import ru.laurkan.bank.transfer.mapper.TransactionMapperImpl;
import ru.laurkan.bank.transfer.model.TransactionType;
import ru.laurkan.bank.transfer.repository.TransactionRepository;
import ru.laurkan.bank.transfer.repository.dto.TransactionRepositoryDTO;
import ru.laurkan.bank.transfer.service.impl.TransactionServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@WebFluxTest({TransactionServiceImpl.class, TransactionMapperImpl.class})
@ActiveProfiles("test")
public class TransactionServiceTest {
    @MockitoBean
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        reset(transactionRepository);
    }

    @Test
    public void create_shouldCreateSelfTransferTransaction() {
        var request = new CreateTransactionRequestDTO();
        request.setAccountId(1L);
        request.setAmount(10.0);

        var transaction = new TransactionRepositoryDTO();
        transaction.setAccountId(1L);
        transaction.setAmount(10.0);
        transaction.setTransactionType(TransactionType.SELF_TRANSFER);

        when(transactionRepository.save(any(TransactionRepositoryDTO.class)))
                .thenReturn(Mono.just(transaction));

        StepVerifier.create(transactionService.create(TransactionType.SELF_TRANSFER, request))
                .assertNext(transactionResponseDTO -> assertThat(transactionResponseDTO)
                        .isNotNull()
                        .isInstanceOf(TransactionResponseDTO.class)
                        .extracting(TransactionResponseDTO::getTransactionType)
                        .isEqualTo(TransactionType.SELF_TRANSFER)
                )
                .verifyComplete();
    }

    @Test
    public void create_shouldCreateTransferToOtherUserTransaction() {
        var request = new CreateTransactionRequestDTO();
        request.setAccountId(1L);
        request.setAmount(10.0);

        var transaction = new TransactionRepositoryDTO();
        transaction.setAccountId(1L);
        transaction.setAmount(10.0);
        transaction.setTransactionType(TransactionType.TRANSFER_TO_OTHER_USER);

        when(transactionRepository.save(any(TransactionRepositoryDTO.class)))
                .thenReturn(Mono.just(transaction));

        StepVerifier.create(transactionService.create(TransactionType.TRANSFER_TO_OTHER_USER, request))
                .assertNext(transactionResponseDTO -> assertThat(transactionResponseDTO)
                        .isNotNull()
                        .isInstanceOf(TransactionResponseDTO.class)
                        .extracting(TransactionResponseDTO::getTransactionType)
                        .isEqualTo(TransactionType.TRANSFER_TO_OTHER_USER)
                )
                .verifyComplete();
    }
}
