package ru.laurkan.bank.accounts.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.laurkan.bank.accounts.dto.account.AccountResponseDTO;
import ru.laurkan.bank.accounts.dto.account.CreateAccountRequestDTO;
import ru.laurkan.bank.accounts.exception.AccountHasMoneyException;
import ru.laurkan.bank.accounts.exception.NotFoundException;
import ru.laurkan.bank.accounts.exception.UserAlreadyHasAccountInThisCurrency;
import ru.laurkan.bank.accounts.mapper.AccountMapper;
import ru.laurkan.bank.accounts.model.Account;
import ru.laurkan.bank.accounts.model.Currency;
import ru.laurkan.bank.accounts.repository.AccountRepository;
import ru.laurkan.bank.accounts.service.AccountService;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

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
    public Flux<AccountResponseDTO> readAccountsOfUser(Long userId) {
        return accountRepository.findByUserId(userId)
                .map(accountMapper::map);
    }
}
