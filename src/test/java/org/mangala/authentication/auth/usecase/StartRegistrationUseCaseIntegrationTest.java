package org.mangala.authentication.auth.usecase;

import org.junit.jupiter.api.Test;
import org.mangala.authentication.passkey.adapter.repository.PasskeyChallengeRepository;
import org.mangala.authentication.passkey.domain.PasskeyChallengeOperationType;
import org.mangala.authentication.user.adapter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
class StartRegistrationUseCaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("mangala_auth_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl() + "&currentSchema=auth");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.flyway.schemas", () -> "auth");
        registry.add("spring.flyway.default-schema", () -> "auth");

        registry.add("application.authentication.passkey.rp.id", () -> "localhost");
        registry.add("application.authentication.passkey.rp.name", () -> "localhost");
        registry.add("application.authentication.passkey.rp.origin", () -> "http://localhost:5173");
        registry.add("application.authentication.passkey.timeout", () -> 300000L);
        registry.add("application.authentication.passkey.algorithms", () -> "ES256,RS256");
        registry.add("application.authentication.passkey.authenticator.attachment", () -> "platform");
        registry.add("application.authentication.passkey.authenticator.resident-key", () -> true);
        registry.add("application.authentication.passkey.authenticator.user-verification", () -> "preferred");
        registry.add("application.authentication.passkey.attestation", () -> "none");
    }

    @Autowired
    private StartRegistrationUseCase startRegistrationUseCase;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasskeyChallengeRepository passkeyChallengeRepository;

    @Test
    void execute_shouldPersistUserAndChallengeWithConsistentUserId() {
        String email = "integration-test@gmail.com";

        var response = startRegistrationUseCase.execute(email, "127.0.0.1", "integration-test-agent");

        assertNotNull(response);
        assertNotNull(response.getSessionId());
        assertNotNull(response.getUser());

        UUID userIdFromResponse = UUID.fromString(
            new String(response.getUser().getId(), StandardCharsets.UTF_8)
        );

        var savedUser = userRepository.findById(userIdFromResponse).orElseThrow();
        assertEquals(email, savedUser.getEmail());

        var savedChallenge = passkeyChallengeRepository.findBySessionId(response.getSessionId()).orElseThrow();
        assertEquals(userIdFromResponse, savedChallenge.getUserId());
        assertEquals(PasskeyChallengeOperationType.REGISTER, savedChallenge.getOperationType());
        assertFalse(savedChallenge.getIsUsed());
    }
}
