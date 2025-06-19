package ru.laurkan.bank.accounts.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.InternalException;
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

@Log4j2
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
        var account = new Account(createAccountRequestDTO.getUserId(),
                Currency.valueOf(createAccountRequestDTO.getCurrency()));
        log.debug("create account request received: {}", createAccountRequestDTO);
        return saveAccountToDb(account)
                .doOnError(e -> {
                    log.error("ошибка сохранения аккаунта в бд: {}", e.getMessage());
                })
                .doOnSuccess(ac -> {
                    sendDomainEvent(OUTPUT_ACCOUNT_EVENTS_TOPIC, ac.getId(),
                            accountMapper.event(ac));
                })
                .doOnSuccess(accountSaved -> {
                    sendNotification(ACCOUNT_NOTIFICATION_EVENTS_TOPIC, account.getUserId(),
                            new AccountEvent(AccountEventType.ACCOUNT_CREATED, account.getId()));
                })
                .map(accountMapper::map)
                .doOnNext(accountResponseDTO -> {
                    log.info("account successfully created id = {} currency = {}", accountResponseDTO.getId(),
                            accountResponseDTO.getCurrency());
                });
    }

    @Override
    public Mono<Void> deleteAccount(Long id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> {
                    log.error("Не найден счет (id = {})", id);
                    return new AccountNotFoundException(id);
                }))
                .doOnNext(account -> {
                    if (account.getAmount() != 0) {
                        log.error("Нельзя удалить счет, так как на нем есть деньги (id = {})", id);
                        throw new AccountHasMoneyException();
                    }
                })
                .flatMap(__ -> accountRepository
                        .deleteById(id))
                .doOnSuccess(__ -> {
                    log.info("account successfully deleted id = {}", id);
                });
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
                .switchIfEmpty(Mono.error(() -> {
                    log.error("Не найден счет для пополнения (id = {})", accountId);
                    return new AccountNotFoundException(accountId);
                }))
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
                .doOnError(e -> {
                    log.error("Ошибка сохраниения аккаунта в бд, после пополнения (id = {})", accountId);
                })
                .map(accountMapper::map)
                .doOnNext(accountResponseDTO ->
                        log.info("аккаунт успешно пополнен id = {}", accountId));
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

    private Mono<Account> saveAccountToDb(Account account) {
            return accountRepository.save(account)
                    .doOnError(e -> {
                        if (e instanceof DuplicateKeyException) {
                             throw new UserAlreadyHasAccountInThisCurrency(account.getCurrency());
                        }

                        throw new InternalException(e.getMessage());
                    });

    }

    private void sendDomainEvent(String topic, Long key, AccountInfo event) {
        Mono.fromFuture(domainEvents.send(topic, key, event))
                .doOnSuccess(result -> log.debug("event sent to kafka, offset - {}",
                        result.getRecordMetadata().offset()))
                .doOnError(e -> {
                    log.error("error sending event to kafka {}", e.getMessage());
                    throw new SendEventException(e.getMessage());
                })
                .subscribe();
    }

    private void sendNotification(String topic, Long key, AccountEvent event) {
        Mono.fromFuture(notifications.send(topic, key, event))
                .doOnSuccess(result -> log.debug("notification sent to kafka, offset - {}",
                        result.getRecordMetadata().offset()))
                .doOnError(e -> {
                    log.error("error sending notification to kafka {}", e.getMessage());
                    throw new SendEventException(e.getMessage());
                })
                .subscribe();
    }
}
