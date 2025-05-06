package ru.laurkan.bank.blocker.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelfTransferTransaction extends Transaction {
    private Long receiverAccountId;
}
