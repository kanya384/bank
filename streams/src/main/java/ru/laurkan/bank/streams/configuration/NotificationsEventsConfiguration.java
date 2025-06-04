package ru.laurkan.bank.streams.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.laurkan.bank.events.accounts.AccountDetailedEvent;
import ru.laurkan.bank.events.accounts.AccountEvent;
import ru.laurkan.bank.events.accounts.AccountInfo;
import ru.laurkan.bank.events.cash.CashEvent;
import ru.laurkan.bank.events.cash.CashEventDetailed;
import ru.laurkan.bank.events.transfer.TransferEvent;
import ru.laurkan.bank.events.transfer.TransferEventDetailed;
import ru.laurkan.bank.events.users.UserDetailedEvent;
import ru.laurkan.bank.events.users.UserEvent;
import ru.laurkan.bank.events.users.UserInfo;

@Configuration
public class NotificationsEventsConfiguration {
    public static final String INPUT_ACCOUNT_EVENTS_TOPIC = "account-events";
    public static final String INPUT_USER_EVENTS_TOPIC = "user-events";
    public static final String CASH_NOTIFICATION_EVENTS_TOPIC = "cash-notification-events";
    public static final String TRANSFER_NOTIFICATION_EVENTS_TOPIC = "transfer-notification-events";
    public static final String ACCOUNT_NOTIFICATION_EVENTS_TOPIC = "account-notification-events";
    public static final String USER_NOTIFICATION_EVENTS_TOPIC = "user-notification-events";
    public static final String ACCOUNT_OUTPUT_DETAILED_EVENTS_TOPIC = "account-detailed-events";
    public static final String USER_OUTPUT_DETAILED_EVENTS_TOPIC = "user-detailed-events";
    public static final String CASH_OUTPUT_DETAILED_EVENTS_TOPIC = "cash-detailed-events";
    public static final String TRANSFER_OUTPUT_DETAILED_EVENTS_TOPIC = "transfer-detailed-events";

    /**
     * Принимает события аккаунта, требующие нотификаций пользователю, из топика account-notification-events
     * (ключ — ID профиля, значение - тип произошедшего события)
     */
    @Bean
    public KStream<Long, AccountEvent> accountCreatedStream(StreamsBuilder builder) {
        return builder.stream(ACCOUNT_NOTIFICATION_EVENTS_TOPIC,
                Consumed.with(
                        Serdes.Long(),
                        new JsonSerde<>(AccountEvent.class)
                )
        );
    }

    /**
     * Принимает события изменения пароля, требующие нотификаций пользователю, из топика user-notification-events
     * (ключ — ID профиля, значение - тип произошедшего события)
     */
    @Bean
    public KStream<Long, UserEvent> userEventsStream(StreamsBuilder builder) {
        return builder.stream(USER_NOTIFICATION_EVENTS_TOPIC,
                Consumed.with(
                        Serdes.Long(),
                        new JsonSerde<>(UserEvent.class)
                )
        );
    }

    /**
     * Принимает события аккаунта, требующие нотификаций пользователю, из топика account-notification-events
     * (ключ — ID профиля, значение - тип произошедшего события)
     */
    @Bean
    public KStream<Long, CashEvent> cashEventsStream(StreamsBuilder builder) {
        return builder.stream(CASH_NOTIFICATION_EVENTS_TOPIC,
                Consumed.with(
                        Serdes.Long(),
                        new JsonSerde<>(CashEvent.class)
                )
        );
    }

    /**
     * Принимает события аккаунта, требующие нотификаций пользователю, из топика account-notification-events
     * (ключ — ID профиля, значение - тип произошедшего события)
     */
    @Bean
    public KStream<Long, TransferEvent> transferEventsStream(StreamsBuilder builder) {
        return builder.stream(TRANSFER_NOTIFICATION_EVENTS_TOPIC,
                Consumed.with(
                        Serdes.Long(),
                        new JsonSerde<>(TransferEvent.class)
                )
        );
    }

    /**
     * Принимает обновления профилей пользователей из топика profile-events
     * (ключ — ID профиля, значение - последний профиль)
     */
    @Bean
    public KTable<Long, UserInfo> userProfileUpdatesTable(StreamsBuilder builder) {
        return builder.table(INPUT_USER_EVENTS_TOPIC,
                Consumed.with(
                        Serdes.Long(),
                        new JsonSerde<>(UserInfo.class)
                )
        );
    }

    /**
     * Принимает обновления счетов пользователей из топика profile-events
     * (ключ — ID профиля, значение - последний профиль)
     */
    @Bean
    public KTable<Long, AccountInfo> accountUpdatesTable(StreamsBuilder builder) {
        return builder.table(INPUT_ACCOUNT_EVENTS_TOPIC,
                Consumed.with(
                        Serdes.Long(),
                        new JsonSerde<>(AccountInfo.class)
                )
        );
    }

    /**
     * Объединяющий стрим для AccountEvents
     */
    @Bean
    public KStream<Long, AccountDetailedEvent> accountEventsDetailedStream(
            KTable<Long, UserInfo> userProfileUpdatesTable,
            KStream<Long, AccountEvent> accountEventsStream
    ) {

        var stream = accountEventsStream
                .leftJoin(
                        userProfileUpdatesTable,
                        AccountDetailedEvent::new,
                        Joined.with(
                                Serdes.Long(),
                                new JsonSerde<>(AccountEvent.class),
                                new JsonSerde<>(UserInfo.class)
                        ));

        stream.to(ACCOUNT_OUTPUT_DETAILED_EVENTS_TOPIC);
        return stream;
    }

    /**
     * Объединяющий стрим для UserEvents
     */
    @Bean
    public KStream<Long, UserDetailedEvent> userEventsDetailedStream(
            KTable<Long, UserInfo> userProfileUpdatesTable,
            KStream<Long, UserEvent> userEventsStream
    ) {
        var stream = userEventsStream
                .leftJoin(
                        userProfileUpdatesTable,
                        UserDetailedEvent::new,
                        Joined.with(
                                Serdes.Long(),
                                new JsonSerde<>(UserEvent.class),
                                new JsonSerde<>(UserInfo.class)
                        ));

        stream.to(USER_OUTPUT_DETAILED_EVENTS_TOPIC);
        return stream;
    }

    /**
     * Объединяющий стрим для CashEvents
     */
    @Bean
    public KStream<Long, CashEventDetailed> cashEventsDetailedStream(
            KTable<Long, AccountInfo> accountInfoKTable,
            KTable<Long, UserInfo> userProfileUpdatesTable,
            KStream<Long, CashEvent> cashEventKStream
    ) {
        record CashEventWithAccount(
                CashEvent cashEvent,
                AccountInfo accountInfo
        ) {
        }

        var stream = cashEventKStream
                .leftJoin(
                        accountInfoKTable,
                        CashEventWithAccount::new,
                        Joined.with(
                                Serdes.Long(),
                                new JsonSerde<>(CashEvent.class),
                                new JsonSerde<>(AccountInfo.class)
                        ))
                .selectKey((key, eventWithAccount) ->
                        eventWithAccount.accountInfo().userId())
                .mapValues(CashEventWithAccount::cashEvent)
                .leftJoin(
                        userProfileUpdatesTable,
                        CashEventDetailed::new,
                        Joined.with(
                                Serdes.Long(),
                                new JsonSerde<>(CashEvent.class),
                                new JsonSerde<>(UserInfo.class)
                        )
                );

        stream.to(CASH_OUTPUT_DETAILED_EVENTS_TOPIC);
        return stream;
    }

    /**
     * Объединяющий стрим для TransferEvents
     */
    @Bean
    public KStream<Long, TransferEventDetailed> transferEventsDetailedStream(
            KTable<Long, AccountInfo> accountInfoKTable,
            KTable<Long, UserInfo> userProfileUpdatesTable,
            KStream<Long, TransferEvent> transferEventKStream
    ) {
        record TransferEventWithAccount(
                TransferEvent transferEvent,
                AccountInfo accountInfo
        ) {
        }

        var stream = transferEventKStream
                .leftJoin(
                        accountInfoKTable,
                        TransferEventWithAccount::new,
                        Joined.with(
                                Serdes.Long(),
                                new JsonSerde<>(TransferEvent.class),
                                new JsonSerde<>(AccountInfo.class)
                        ))
                .selectKey((key, eventWithAccount) ->
                        eventWithAccount.accountInfo().userId())
                .mapValues(TransferEventWithAccount::transferEvent)
                .leftJoin(
                        userProfileUpdatesTable,
                        TransferEventDetailed::new,
                        Joined.with(
                                Serdes.Long(),
                                new JsonSerde<>(TransferEvent.class),
                                new JsonSerde<>(UserInfo.class)
                        )
                );

        stream.to(TRANSFER_OUTPUT_DETAILED_EVENTS_TOPIC);
        return stream;
    }
}
