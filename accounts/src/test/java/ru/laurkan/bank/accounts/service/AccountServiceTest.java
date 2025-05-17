package ru.laurkan.bank.accounts.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.laurkan.bank.accounts.dto.account.AccountResponseDTO;
import ru.laurkan.bank.accounts.dto.account.CreateAccountRequestDTO;
import ru.laurkan.bank.accounts.dto.account.TransferMoneyRequestDTO;
import ru.laurkan.bank.accounts.dto.account.TransferMoneyResponseDTO;
import ru.laurkan.bank.accounts.exception.*;
import ru.laurkan.bank.accounts.mapper.AccountMapperImpl;
import ru.laurkan.bank.accounts.model.Account;
import ru.laurkan.bank.accounts.model.Currency;
import ru.laurkan.bank.accounts.repository.AccountRepository;
import ru.laurkan.bank.accounts.service.impl.AccountServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@WebFluxTest({AccountServiceImpl.class, AccountMapperImpl.class})
@ActiveProfiles("test")
public class AccountServiceTest {
    @MockitoBean
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        reset(accountRepository);
    }

    @Test
    public void create_shouldCreateAccount() {
        var createRequest = new CreateAccountRequestDTO();
        createRequest.setCurrency("RUB");
        createRequest.setUserId(1L);

        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .currency(Currency.RUB)
                        .amount(0.0)
                        .build()));

        accountService.create(createRequest)
                .doOnNext(accountResponseDTO -> Assertions.assertThat(accountResponseDTO)
                        .isNotNull()
                        .extracting(AccountResponseDTO::getId)
                        .isEqualTo(1L)
                );
    }

    @Test
    public void create_shouldThrowExceptionIfUserAlreadyExistsAccountInCurrency() {
        var createRequest = new CreateAccountRequestDTO();
        createRequest.setCurrency("RUB");
        createRequest.setUserId(1L);


        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.error(new DuplicateKeyException("")));

        StepVerifier.create(accountService.create(createRequest))
                .expectErrorMatches(exception -> exception instanceof UserAlreadyHasAccountInThisCurrency)
                .verify();
    }

    @Test
    public void deleteAccount_shouldDeleteAccount() {
        when(accountRepository.findById(any(Long.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .currency(Currency.RUB)
                        .amount(0.0)
                        .build()));

        when(accountRepository.deleteById(any(Long.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(accountService.deleteAccount(1L))
                .verifyComplete();
    }

    @Test
    public void deleteAccount_shouldThrowExceptionIfAccountNotExists() {
        when(accountRepository.findById(any(Long.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(accountService.deleteAccount(3L))
                .expectErrorMatches(exception -> exception instanceof NotFoundException);
    }

    @Test
    public void deleteAccount_shouldThrowExceptionIfAccountHasMoney() {
        when(accountRepository.findById(any(Long.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .currency(Currency.RUB)
                        .amount(1.0)
                        .build()));

        StepVerifier.create(accountService.deleteAccount(1L))
                .expectErrorMatches(exception -> exception instanceof AccountHasMoneyException);
    }

    @Test
    public void readAccountById_shouldReturnAccountById() {
        when(accountRepository.findById(any(Long.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .currency(Currency.RUB)
                        .amount(1.0)
                        .build()));

        StepVerifier.create(accountService.readAccountById(1L))
                .assertNext(accountResponseDTO -> Assertions.assertThat(accountResponseDTO)
                        .withFailMessage("Аккаунт не должен быть null")
                        .isNotNull()
                        .extracting(AccountResponseDTO::getId)
                        .withFailMessage("Id не равен ожидаемому")
                        .isEqualTo(1L)
                )
                .verifyComplete();
    }

    @Test
    public void readAccountsOfUser_shouldReturnAccountsOfUser() {
        var account1 = Account.builder()
                .id(1L)
                .userId(1L)
                .currency(Currency.RUB)
                .amount(1.0)
                .build();

        var account2 = Account.builder()
                .id(1L)
                .userId(1L)
                .currency(Currency.USD)
                .amount(1.0)
                .build();

        when(accountRepository.findByUserId(any(Long.class)))
                .thenReturn(Flux.just(account1, account2));

        StepVerifier.create(accountService.readAccountsOfUser(1L)
                        .collectList())
                .assertNext(accountResponseDTO -> Assertions.assertThat(accountResponseDTO)
                        .withFailMessage("Список счетов пользователя не должен быть пустым")
                        .isNotNull()
                        .withFailMessage("Количество счетов пользователя должно быть равно 2")
                        .hasSize(2)
                )
                .verifyComplete();
    }

    @Test
    public void putMoneyToAccount_shouldPutMoneyToAccount() {
        when(accountRepository.findById(any(Long.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(10.0)
                        .build()));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(20.0)
                        .build()));

        StepVerifier.create(accountService.putMoneyToAccount(1L, 10.0))
                .assertNext(accountResponseDTO -> Assertions.assertThat(accountResponseDTO)
                        .withFailMessage("Аккаунт не должен быть пустым")
                        .isNotNull()
                        .withFailMessage("Сумма на аккаунте после обновления должна равнятся 20")
                        .extracting(AccountResponseDTO::getAmount)
                        .isEqualTo(20.0)
                )
                .verifyComplete();
    }

    @Test
    public void putMoneyToAccount_shouldThrowExceptionIfNoAccount() {
        when(accountRepository.findById(any(Long.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(accountService.putMoneyToAccount(1L, 10.0))
                .expectErrorMatches(exception -> exception instanceof AccountNotFoundException)
                .verify();
    }

    @Test
    public void takeMoneyFromAccount_shouldTakeMoneyFromAccount() {
        when(accountRepository.findById(any(Long.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(10.0)
                        .build()));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(0.0)
                        .build()));

        StepVerifier.create(accountService.takeMoneyFromAccount(1L, 10.0))
                .assertNext(accountResponseDTO -> Assertions.assertThat(accountResponseDTO)
                        .withFailMessage("Аккаунт не должен быть пустым")
                        .isNotNull()
                        .withFailMessage("Сумма на аккаунте после обновления должна равнятся 0")
                        .extracting(AccountResponseDTO::getAmount)
                        .isEqualTo(0.0)
                )
                .verifyComplete();
    }

    @Test
    public void takeMoneyFromAccount_shouldThrowExceptionIfNotFoundAccount() {
        when(accountRepository.findById(any(Long.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(accountService.takeMoneyFromAccount(1L, 30.0))
                .expectErrorMatches(exception -> exception instanceof NotFoundException)
                .verify();
    }

    @Test
    public void takeMoneyFromAccount_shouldThrowExceptionIfNotEnoughMoney() {
        when(accountRepository.findById(any(Long.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(10.0)
                        .build()));

        StepVerifier.create(accountService.takeMoneyFromAccount(1L, 30.0))
                .expectErrorMatches(exception -> exception instanceof NotEnoughMoneyException)
                .verify();
    }

    @Test
    public void transferMoney_shouldThrowExceptionIfNotEnoughMoneyOnFromAccount() {
        var request = new TransferMoneyRequestDTO();
        request.setFromAccountId(1L);
        request.setFromMoneyAmount(30.0);
        request.setToAccountId(2L);
        request.setToMoneyAmount(20.0);

        when(accountRepository.findById(any(Long.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(10.0)
                        .build()));

        StepVerifier.create(accountService.transferMoney(request))
                .expectErrorMatches(exception -> exception instanceof NotEnoughMoneyException)
                .verify();
    }

    @Test
    public void transferMoney_shouldThrowExceptionIfNoReceiverAccount() {
        var request = new TransferMoneyRequestDTO();
        request.setFromAccountId(1L);
        request.setFromMoneyAmount(30.0);
        request.setToAccountId(2L);
        request.setToMoneyAmount(20.0);

        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(50.0)
                        .build()));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(20.0)
                        .build()));

        when(accountRepository.findById(2L))
                .thenReturn(Mono.empty());

        StepVerifier.create(accountService.transferMoney(request))
                .expectErrorMatches(exception -> exception instanceof AccountNotFoundException)
                .verify();
    }

    @Test
    public void transferMoney_shouldTransferMoney() {
        var request = new TransferMoneyRequestDTO();
        request.setFromAccountId(1L);
        request.setFromMoneyAmount(30.0);
        request.setToAccountId(2L);
        request.setToMoneyAmount(20.0);

        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(50.0)
                        .build()));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(20.0)
                        .build()));

        when(accountRepository.findById(2L))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(20.0)
                        .build()));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(Account.builder()
                        .id(1L)
                        .userId(1L)
                        .amount(40.0)
                        .build()));

        StepVerifier.create(accountService.transferMoney(request))
                .assertNext(transferMoneyResponseDTO -> Assertions.assertThat(transferMoneyResponseDTO)
                        .withFailMessage("Ответ не должен быть пустым")
                        .isNotNull()
                        .withFailMessage("Сумма на аккаунте после обновления должна равнятся 0")
                        .extracting(TransferMoneyResponseDTO::isCompleted)
                        .isEqualTo(true)
                )
                .verifyComplete();
    }
}
