package ru.laurkan.bank.accounts;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static ru.laurkan.bank.accounts.configuration.KafkaConfiguration.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port = 0"
)
@EmbeddedKafka(
        topics = {
                OUTPUT_ACCOUNT_EVENTS_TOPIC,
                OUTPUT_USER_EVENTS_TOPIC,
                ACCOUNT_NOTIFICATION_EVENTS_TOPIC,
                USER_NOTIFICATION_EVENTS_TOPIC
        }
)
@Testcontainers
@ActiveProfiles("test")
@Import(SecurityTestConfiguration.class)
public class AbstractTestContainer {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:17.4");
}
