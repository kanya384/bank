package ru.laurkan.bank.notifications;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port = 0"
)
@Testcontainers
@Import(SecurityTestConfiguration.class)
public class AbstractTestContainer {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:17.4");

}
