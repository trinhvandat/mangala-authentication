package org.mangala.authentication.auth.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mangala.authentication.passkey.domain.PasskeyChallengeOperationType;
import org.mangala.authentication.passkey.domain.PasskeyChallengeEntity;
import org.mangala.authentication.passkey.usecase.CreatePasskeyChallengeUseCase;
import org.mangala.authentication.passkey.usecase.command.CreatePasskeyChallengeCommand;
import org.mangala.authentication.shared.config.WebAuthnConfigProperties;
import org.mangala.authentication.user.domain.UserEntity;
import org.mangala.authentication.user.usecase.CheckUserExistedUseCase;
import org.mangala.authentication.user.usecase.CreateUserUseCase;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

class StartRegistrationUseCaseImplTest {

    private FakeCheckUserExistedUseCase checkUserExistedUseCase;
    private FakeCreateUserUseCase createUserUseCase;
    private FakeCreatePasskeyChallengeUseCase createPasskeyChallengeUseCase;
    private WebAuthnConfigProperties webAuthnConfigProperties;
    private StartRegistrationUseCaseImpl startRegistrationUseCase;

    @BeforeEach
    void setUp() {
        checkUserExistedUseCase = new FakeCheckUserExistedUseCase(false);
        createUserUseCase = new FakeCreateUserUseCase();
        createPasskeyChallengeUseCase = new FakeCreatePasskeyChallengeUseCase();

        webAuthnConfigProperties = new WebAuthnConfigProperties();

        WebAuthnConfigProperties.RelyingParty rp = new WebAuthnConfigProperties.RelyingParty();
        rp.setId("localhost");
        rp.setName("localhost");

        WebAuthnConfigProperties.Authenticator authenticator = new WebAuthnConfigProperties.Authenticator();
        authenticator.setUserVerification("preferred");

        webAuthnConfigProperties.setRp(rp);
        webAuthnConfigProperties.setTimeout(300000L);
        webAuthnConfigProperties.setAuthenticator(authenticator);

        startRegistrationUseCase = new StartRegistrationUseCaseImpl(
            webAuthnConfigProperties,
            checkUserExistedUseCase,
            createUserUseCase,
            createPasskeyChallengeUseCase
        );
    }

    @Test
    void execute_shouldCreateUserAndPersistChallengeWithSameUserId() {
        String email = "dat@gmail.com";

        var response = startRegistrationUseCase.execute(email, "127.0.0.1", "JUnit");

        assertNotNull(response);
        assertEquals(email, response.getUser().getName());
        assertEquals(email, response.getUser().getDisplayName());
        assertTrue(createUserUseCase.called);
        assertEquals(email, createUserUseCase.capturedEmail);
        assertNull(createUserUseCase.capturedUserId);
        assertEquals(
            createUserUseCase.persistedUserId.toString(),
            new String(response.getUser().getId(), StandardCharsets.UTF_8)
        );

        assertNotNull(createPasskeyChallengeUseCase.capturedCommand);
        CreatePasskeyChallengeCommand challengeCommand = createPasskeyChallengeUseCase.capturedCommand;
        assertEquals(PasskeyChallengeOperationType.REGISTER, challengeCommand.getOperationType());
        assertEquals(createUserUseCase.persistedUserId.toString(), challengeCommand.getUserId());
    }

    private static class FakeCheckUserExistedUseCase implements CheckUserExistedUseCase {
        private final boolean existed;

        private FakeCheckUserExistedUseCase(boolean existed) {
            this.existed = existed;
        }

        @Override
        public boolean execute(String email) {
            return existed;
        }
    }

    private static class FakeCreateUserUseCase implements CreateUserUseCase {
        private boolean called;
        private String capturedEmail;
        private UUID capturedUserId;
        private UUID persistedUserId;

        @Override
        public UserEntity execute(String email, UUID userId) {
            called = true;
            capturedEmail = email;
            capturedUserId = userId;
            persistedUserId = UUID.randomUUID();

            UserEntity userEntity = new UserEntity();
            userEntity.setId(persistedUserId);
            userEntity.setEmail(email);
            return userEntity;
        }
    }

    private static class FakeCreatePasskeyChallengeUseCase implements CreatePasskeyChallengeUseCase {
        private CreatePasskeyChallengeCommand capturedCommand;

        @Override
        public PasskeyChallengeEntity execute(CreatePasskeyChallengeCommand command) {
            capturedCommand = command;
            return new PasskeyChallengeEntity();
        }
    }
}
