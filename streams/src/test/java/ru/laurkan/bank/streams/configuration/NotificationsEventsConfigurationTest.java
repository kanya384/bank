package ru.laurkan.bank.streams.configuration;

import org.apache.kafka.common.serialization.LongDeserializer;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import ru.laurkan.bank.events.accounts.AccountDetailedEvent;
import ru.laurkan.bank.events.accounts.AccountEvent;
import ru.laurkan.bank.events.accounts.AccountEventType;
import ru.laurkan.bank.events.accounts.AccountInfo;
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

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.kafka.test.hamcrest.KafkaMatchers.hasKey;
import static org.springframework.kafka.test.hamcrest.KafkaMatchers.hasValue;
import static ru.laurkan.bank.streams.configuration.NotificationsEventsConfiguration.*;

@SpringBootTest
@EmbeddedKafka(
        topics = {
                INPUT_ACCOUNT_EVENTS_TOPIC,
                INPUT_USER_EVENTS_TOPIC,
                CASH_NOTIFICATION_EVENTS_TOPIC,
                TRANSFER_NOTIFICATION_EVENTS_TOPIC,
                ACCOUNT_NOTIFICATION_EVENTS_TOPIC,
                USER_NOTIFICATION_EVENTS_TOPIC,
                ACCOUNT_OUTPUT_DETAILED_EVENTS_TOPIC,
                USER_OUTPUT_DETAILED_EVENTS_TOPIC,
                CASH_OUTPUT_DETAILED_EVENTS_TOPIC,
                TRANSFER_OUTPUT_DETAILED_EVENTS_TOPIC
        }
)
@ActiveProfiles("test")
public class NotificationsEventsConfigurationTest {
    @Autowired
    private KafkaTemplate<Long, Object> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    @DisplayName("Проверка успешного добавления профиля в событие CashEvent")
    public void testCashEventToProfileMerging() {
        try (var consumerForTest = new DefaultKafkaConsumerFactory<>(
                KafkaTestUtils.consumerProps("streams-group", "true", embeddedKafkaBroker),
                new LongDeserializer(),
                new JsonDeserializer<>(CashEventDetailed.class)
        ).createConsumer()) {
            consumerForTest.subscribe(List.of(CASH_OUTPUT_DETAILED_EVENTS_TOPIC));

            var testUserInfo = new UserInfo(1L, "login", "surname", "name", "test01@mail.ru",
                    LocalDate.of(1990, 6, 20));
            kafkaTemplate.send(INPUT_USER_EVENTS_TOPIC, testUserInfo.id(), testUserInfo);

            var testAccountInfo = new AccountInfo(2L, 1L, "RUB", 10.0);
            kafkaTemplate.send(INPUT_ACCOUNT_EVENTS_TOPIC, testAccountInfo.id(), testAccountInfo);

            var cashEvent = new CashEvent(CashEventType.DEPOSIT_TRANSACTION, 2L, 5.0,
                    TransactionEventStatus.COMPLETED);

            kafkaTemplate.send(CASH_NOTIFICATION_EVENTS_TOPIC, cashEvent.accountId(), cashEvent);

            var outputMessage = KafkaTestUtils.getSingleRecord(consumerForTest,
                    CASH_OUTPUT_DETAILED_EVENTS_TOPIC, Duration.ofSeconds(5));

            var expectedMergedEvent = new CashEventDetailed(cashEvent, testUserInfo);
            MatcherAssert.assertThat(outputMessage, hasKey(testUserInfo.id()));
            MatcherAssert.assertThat(outputMessage, hasValue(expectedMergedEvent));
        }
    }

    @Test
    @DisplayName("Проверка успешного добавления профиля в событие TransferEvent")
    public void testTransferEventToProfileMerging() {
        try (var consumerForTest = new DefaultKafkaConsumerFactory<>(
                KafkaTestUtils.consumerProps("streams-group", "true", embeddedKafkaBroker),
                new LongDeserializer(),
                new JsonDeserializer<>(TransferEventDetailed.class)
        ).createConsumer()) {
            consumerForTest.subscribe(List.of(TRANSFER_OUTPUT_DETAILED_EVENTS_TOPIC));

            var testUserInfo = new UserInfo(1L, "login", "surname", "name", "test01@mail.ru",
                    LocalDate.of(1990, 6, 20));
            kafkaTemplate.send(INPUT_USER_EVENTS_TOPIC, testUserInfo.id(), testUserInfo);

            var testAccountInfo = new AccountInfo(2L, 1L, "RUB", 10.0);
            kafkaTemplate.send(INPUT_ACCOUNT_EVENTS_TOPIC, testAccountInfo.id(), testAccountInfo);

            var transferEvent = new TransferEvent(TransferEventType.SELF_TRANSFER, 2L, 5.0, 1L,
                    TransactionEventStatus.COMPLETED);

            kafkaTemplate.send(TRANSFER_NOTIFICATION_EVENTS_TOPIC, transferEvent.fromAccountId(), transferEvent);

            var outputMessage = KafkaTestUtils.getSingleRecord(consumerForTest,
                    TRANSFER_OUTPUT_DETAILED_EVENTS_TOPIC, Duration.ofSeconds(5));

            var expectedMergedEvent = new TransferEventDetailed(transferEvent, testUserInfo);
            MatcherAssert.assertThat(outputMessage, hasKey(testUserInfo.id()));
            MatcherAssert.assertThat(outputMessage, hasValue(expectedMergedEvent));
        }
    }

    @Test
    @DisplayName("Проверка успешного добавления профиля в событие AccountEvent")
    public void tesAccountEventToProfileMerging() {
        try (var consumerForTest = new DefaultKafkaConsumerFactory<>(
                KafkaTestUtils.consumerProps("streams-group", "true", embeddedKafkaBroker),
                new LongDeserializer(),
                new JsonDeserializer<>(AccountDetailedEvent.class)
        ).createConsumer()) {
            consumerForTest.subscribe(List.of(ACCOUNT_OUTPUT_DETAILED_EVENTS_TOPIC));

            var testUserInfo = new UserInfo(1L, "login", "surname", "name", "test01@mail.ru",
                    LocalDate.of(1990, 6, 20));
            kafkaTemplate.send(INPUT_USER_EVENTS_TOPIC, testUserInfo.id(), testUserInfo);

            var accountEvent = new AccountEvent(AccountEventType.ACCOUNT_CREATED, 2L);

            kafkaTemplate.send(ACCOUNT_NOTIFICATION_EVENTS_TOPIC, testUserInfo.id(), accountEvent);

            var outputMessage = KafkaTestUtils.getSingleRecord(consumerForTest,
                    ACCOUNT_OUTPUT_DETAILED_EVENTS_TOPIC, Duration.ofSeconds(5));

            var expectedMergedEvent = new AccountDetailedEvent(accountEvent, testUserInfo);
            MatcherAssert.assertThat(outputMessage, hasKey(testUserInfo.id()));
            MatcherAssert.assertThat(outputMessage, hasValue(expectedMergedEvent));
        }
    }

    @Test
    @DisplayName("Проверка успешного добавления профиля в событие UserEvent")
    public void tesUserEventToProfileMerging() {
        try (var consumerForTest = new DefaultKafkaConsumerFactory<>(
                KafkaTestUtils.consumerProps("streams-group", "true", embeddedKafkaBroker),
                new LongDeserializer(),
                new JsonDeserializer<>(UserDetailedEvent.class)
        ).createConsumer()) {
            consumerForTest.subscribe(List.of(USER_OUTPUT_DETAILED_EVENTS_TOPIC));

            var testUserInfo = new UserInfo(1L, "login", "surname", "name", "test01@mail.ru",
                    LocalDate.of(1990, 6, 20));
            kafkaTemplate.send(INPUT_USER_EVENTS_TOPIC, testUserInfo.id(), testUserInfo);

            var userEvent = new UserEvent(UserEventType.PASSWORD_CHANGED, 1L);

            kafkaTemplate.send(USER_NOTIFICATION_EVENTS_TOPIC, testUserInfo.id(), userEvent);

            var outputMessage = KafkaTestUtils.getSingleRecord(consumerForTest,
                    USER_OUTPUT_DETAILED_EVENTS_TOPIC, Duration.ofSeconds(5));

            var expectedMergedEvent = new UserDetailedEvent(userEvent, testUserInfo);
            MatcherAssert.assertThat(outputMessage, hasKey(testUserInfo.id()));
            MatcherAssert.assertThat(outputMessage, hasValue(expectedMergedEvent));
        }
    }
}
