package ru.laurkan.bank.notifications;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.test.StepVerifier;
import ru.laurkan.bank.events.accounts.AccountDetailedEvent;
import ru.laurkan.bank.events.accounts.AccountEvent;
import ru.laurkan.bank.events.accounts.AccountEventType;
import ru.laurkan.bank.events.cash.CashEvent;
import ru.laurkan.bank.events.cash.CashEventDetailed;
import ru.laurkan.bank.events.cash.CashEventType;
import ru.laurkan.bank.events.common.TransactionEventStatus;
import ru.laurkan.bank.events.transfer.TransferEvent;
import ru.laurkan.bank.events.transfer.TransferEventDetailed;
import ru.laurkan.bank.events.transfer.TransferEventType;
import ru.laurkan.bank.events.users.UserDetailedEvent;
import ru.laurkan.bank.events.users.UserEvent;
import ru.laurkan.bank.events.users.UserEventType;
import ru.laurkan.bank.events.users.UserInfo;
import ru.laurkan.bank.notifications.consumer.AccountsConsumer;
import ru.laurkan.bank.notifications.consumer.CashConsumer;
import ru.laurkan.bank.notifications.consumer.TransfersConsumer;
import ru.laurkan.bank.notifications.consumer.UsersConsumer;
import ru.laurkan.bank.notifications.model.EmailNotification;
import ru.laurkan.bank.notifications.repository.EmailNotificationRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static ru.laurkan.bank.notifications.configuration.KafkaConfiguration.*;

@DirtiesContext
public class NotificationsIntegrationTest extends AbstractTestContainer {
    @Autowired
    private KafkaTemplate<Long, Object> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockitoSpyBean
    AccountsConsumer accountsConsumer;

    @MockitoSpyBean
    CashConsumer cashConsumer;

    @MockitoSpyBean
    TransfersConsumer transfersConsumer;

    @MockitoSpyBean
    UsersConsumer usersConsumer;

    @MockitoSpyBean
    private EmailNotificationRepository emailNotificationRepository;


    @Test
    @DisplayName("Проверка обработки события AccountEventDetailed")
    public void accountsConsumer_shouldProcessAccountEvent() throws InterruptedException {
        var testAccountDetailedInfo = new AccountDetailedEvent(new AccountEvent(AccountEventType.ACCOUNT_CREATED, 1L),
                new UserInfo(1L, "login", "surname", "name", "test01@mail.ru",
                        LocalDate.of(1990, 6, 20)));
        kafkaTemplate.send(ACCOUNT_INPUT_EVENTS_TOPIC, testAccountDetailedInfo.userInfo().id(), testAccountDetailedInfo);

        ArgumentCaptor<AccountDetailedEvent> eventCaptor =
                ArgumentCaptor.forClass(AccountDetailedEvent.class);

        verify(accountsConsumer, timeout(5000).times(1))
                .processAccountEvent(eventCaptor.capture());

        assertEquals(testAccountDetailedInfo, eventCaptor.getValue());

        verify(emailNotificationRepository, timeout(5000).times(1))
                .save(any(EmailNotification.class));

        StepVerifier.create(emailNotificationRepository.findAll()
                        .collectList())
                .assertNext(emailNotifications -> assertEquals(1, emailNotifications
                        .stream()
                        .filter(emailNotification -> emailNotification.getRecipient()
                                .equals(testAccountDetailedInfo.userInfo().email()) &&
                                emailNotification.getSubject().equals("Открыт новый счет!"))
                        .count())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Проверка обработки события CashEventDetailed")
    public void cashConsumer_shouldProcessCashEvent() throws Exception {
        var testAccountCashEventInfo = new CashEventDetailed(new CashEvent(CashEventType.DEPOSIT_TRANSACTION, 2L,
                20.0, TransactionEventStatus.COMPLETED),
                new UserInfo(2L, "login", "surname", "name", "test02@mail.ru",
                        LocalDate.of(1990, 6, 20)));

        kafkaTemplate.send(CASH_INPUT_EVENTS_TOPIC, testAccountCashEventInfo.userInfo().id(), testAccountCashEventInfo);

        ArgumentCaptor<CashEventDetailed> eventCaptor =
                ArgumentCaptor.forClass(CashEventDetailed.class);

        verify(cashConsumer, timeout(5000).times(1))
                .processTransferEvent(eventCaptor.capture());

        assertEquals(testAccountCashEventInfo, eventCaptor.getValue());

        verify(emailNotificationRepository, timeout(5000).times(1))
                .save(any(EmailNotification.class));

        StepVerifier.create(emailNotificationRepository.findAll()
                        .collectList())
                .assertNext(emailNotifications -> assertEquals(1, emailNotifications
                        .stream()
                        .filter(emailNotification -> emailNotification.getRecipient()
                                .equals(testAccountCashEventInfo.userInfo().email()) &&
                                emailNotification.getSubject().equals("Операция внесения денежных средств"))
                        .count())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Проверка обработки события TransferEventDetailed")
    public void transferConsumer_shouldProcessTransferEvent() throws Exception {
        var testTransferEventInfo = new TransferEventDetailed(new TransferEvent(TransferEventType.TRANSFER_TO_OTHER_USER, 3L,
                20.0, 4L, TransactionEventStatus.COMPLETED),
                new UserInfo(3L, "login", "surname", "name", "test03@mail.ru",
                        LocalDate.of(1990, 6, 20)));

        kafkaTemplate.send(TRANSFER_INPUT_EVENTS_TOPIC, testTransferEventInfo.userInfo().id(), testTransferEventInfo)
                .get();

        ArgumentCaptor<TransferEventDetailed> eventCaptor =
                ArgumentCaptor.forClass(TransferEventDetailed.class);

        verify(transfersConsumer, timeout(5000).times(1))
                .processTransferEvent(eventCaptor.capture());

        assertEquals(testTransferEventInfo, eventCaptor.getValue());

        verify(emailNotificationRepository, timeout(5000).times(1))
                .save(any(EmailNotification.class));

        StepVerifier.create(emailNotificationRepository.findAll()
                        .collectList())
                .assertNext(emailNotifications -> assertEquals(1, emailNotifications
                        .stream()
                        .filter(emailNotification -> emailNotification.getRecipient()
                                .equals(testTransferEventInfo.userInfo().email()) &&
                                emailNotification.getSubject().equals("Результат перевода другому пользователю"))
                        .count())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Проверка обработки события UserDetailedEvent")
    public void userConsumer_shouldProcessUserEvent() throws Exception {
        var testUserEventInfo = new UserDetailedEvent(new UserEvent(UserEventType.PASSWORD_CHANGED, 4L),
                new UserInfo(4L, "login", "surname", "name", "test04@mail.ru",
                        LocalDate.of(1990, 6, 20)));

        kafkaTemplate.send(USER_INPUT_EVENTS_TOPIC, testUserEventInfo.userInfo().id(), testUserEventInfo)
                .get();

        ArgumentCaptor<UserDetailedEvent> eventCaptor =
                ArgumentCaptor.forClass(UserDetailedEvent.class);

        verify(usersConsumer, timeout(10000).times(1))
                .processTransferEvent(eventCaptor.capture());

        assertEquals(testUserEventInfo, eventCaptor.getValue());

        verify(emailNotificationRepository, timeout(10000).times(1))
                .save(any(EmailNotification.class));
    }
}
