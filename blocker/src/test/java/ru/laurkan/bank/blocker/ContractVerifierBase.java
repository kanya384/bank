package ru.laurkan.bank.blocker;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.port = 0"
)
@Import(SecurityTestConfiguration.class)
@ActiveProfiles("test")
public abstract class ContractVerifierBase {
    @LocalServerPort
    int port;

    @BeforeEach
    public void beforeEach() {
        RestAssured.baseURI = "http://localhost:" + this.port;
    }

}
