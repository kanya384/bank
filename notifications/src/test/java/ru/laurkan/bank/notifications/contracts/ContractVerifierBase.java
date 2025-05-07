package ru.laurkan.bank.notifications.contracts;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.laurkan.bank.notifications.AbstractTestContainer;
import ru.laurkan.bank.notifications.repository.EmailNotificationRepository;

@AutoConfigureWebTestClient
@DirtiesContext
@ActiveProfiles("test")
public class ContractVerifierBase extends AbstractTestContainer {
    @LocalServerPort
    int port;

    @Autowired
    private EmailNotificationRepository emailNotificationRepository;

    @BeforeEach
    public void beforeEach() {
        RestAssured.baseURI = "http://localhost:" + this.port;
    }
}
