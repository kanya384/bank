package ru.laurkan.bank.notifications;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static ru.laurkan.bank.notifications.configuration.KafkaConfiguration.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port = 0"
)
@Testcontainers
@ActiveProfiles("test")
@EmbeddedKafka(
        topics = {
                ACCOUNT_INPUT_EVENTS_TOPIC,
                USER_INPUT_EVENTS_TOPIC,
                CASH_INPUT_EVENTS_TOPIC,
                TRANSFER_INPUT_EVENTS_TOPIC
        }
)
@Import(SecurityTestConfiguration.class)
public class AbstractTestContainer {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:17.4");

}
