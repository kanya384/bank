package ru.laurkan.bank.accounts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.accounts.dto.account.AccountResponseDTO;
import ru.laurkan.bank.accounts.dto.account.CreateAccountRequestDTO;
import ru.laurkan.bank.accounts.dto.account.TransferMoneyRequestDTO;
import ru.laurkan.bank.accounts.dto.account.TransferMoneyResponseDTO;
import ru.laurkan.bank.accounts.exception.*;
import ru.laurkan.bank.accounts.mapper.AccountMapper;
import ru.laurkan.bank.accounts.model.Account;
import ru.laurkan.bank.accounts.model.Currency;
import ru.laurkan.bank.accounts.repository.AccountRepository;
import ru.laurkan.bank.accounts.service.AccountService;
import ru.laurkan.bank.events.accounts.AccountEvent;
import ru.laurkan.bank.events.accounts.AccountEventType;
import ru.laurkan.bank.events.accounts.AccountInfo;

import static ru.laurkan.bank.accounts.configuration.KafkaConfiguration.ACCOUNT_NOTIFICATION_EVENTS_TOPIC;
import static ru.laurkan.bank.accounts.configuration.KafkaConfiguration.OUTPUT_ACCOUNT_EVENTS_TOPIC;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final KafkaTemplate<Long, AccountInfo> domainEvents;
    private final KafkaTemplate<Long, AccountEvent> notifications;

    @Transactional
    @Override
    public Mono<AccountResponseDTO> create(CreateAccountRequestDTO createAccountRequestDTO) {
        var currency = Currency.valueOf(createAccountRequestDTO.getCurrency());
        var account = new Account(createAccountRequestDTO.getUserId(),
                Currency.valueOf(createAccountRequestDTO.getCurrency()));


        return accountRepository.save(account)
                .onErrorResume(e -> {
                    if (e instanceof DuplicateKeyException) {
                        return Mono.error(new UserAlreadyHasAccountInThisCurrency(currency));
                    }

                    return Mono.error(e);
                })
                .doOnSuccess(ac -> {
                    try {
                        domainEvents.send(OUTPUT_ACCOUNT_EVENTS_TOPIC, ac.getId(), accountMapper.event(ac))
                                .get();
                    } catch (Exception e) {
                        throw new SendEventException(e.getMessage());
                    }
                })
                .doOnSuccess(accountSaved -> {
                    try {
                        notifications
                                .send(ACCOUNT_NOTIFICATION_EVENTS_TOPIC, account.getUserId(),
                                        new AccountEvent(AccountEventType.ACCOUNT_CREATED, account.getId()))
                                .get();
                    } catch (Exception e) {
                        throw new SendEventException(e.getMessage());
                    }
                })
                .map(accountMapper::map);
    }

    @Override
    public Mono<Void> deleteAccount(Long id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Не найден счет: " + id)))
                .doOnNext(account -> {
                    if (account.getAmount() != 0) {
                        throw new AccountHasMoneyException();
                    }
                })
                .flatMap(__ -> accountRepository
                        .deleteById(id));
    }

    @Override
    public Mono<AccountResponseDTO> readAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
                .map(accountMapper::map);
    }

    @Override
    public Flux<AccountResponseDTO> readAccountsOfUser(Long userId) {
        return accountRepository.findByUserId(userId)
                .map(accountMapper::map);
    }

    @Transactional
    @Override
    public Mono<AccountResponseDTO> putMoneyToAccount(Long accountId, Double amount) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(accountId)))
                .doOnNext(account -> account.setAmount(account.getAmount() + amount))
                .flatMap(accountRepository::save)
                .doOnSuccess(ac -> {
                    try {
                        domainEvents.send(OUTPUT_ACCOUNT_EVENTS_TOPIC, ac.getId(), accountMapper.event(ac))
                                .get();
                    } catch (Exception e) {
                        throw new SendEventException(e.getMessage());
                    }
                })
                .map(accountMapper::map);
    }

    @Transactional
    @Override
    public Mono<AccountResponseDTO> takeMoneyFromAccount(Long accountId, Double amount) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new NotFoundException("Не найден счет: " + accountId)))
                .doOnNext(account -> {
                    if (account.getAmount() < amount) {
                        throw new NotEnoughMoneyException();
                    }
                    account.setAmount(account.getAmount() - amount);
                })
                .flatMap(accountRepository::save)
                .doOnSuccess(ac -> {
                    try {
                        domainEvents.send(OUTPUT_ACCOUNT_EVENTS_TOPIC, ac.getId(), accountMapper.event(ac))
                                .get();
                    } catch (Exception e) {
                        throw new SendEventException(e.getMessage());
                    }
                })
                .map(accountMapper::map);
    }

    @Transactional
    @Override
    public Mono<TransferMoneyResponseDTO> transferMoney(TransferMoneyRequestDTO request) {
        return takeMoneyFromAccount(request.getFromAccountId(), request.getFromMoneyAmount())
                .flatMap(__ -> putMoneyToAccount(request.getToAccountId(), request.getToMoneyAmount()))
                .map(__ -> TransferMoneyResponseDTO.builder().completed(true).build());
    }
}
