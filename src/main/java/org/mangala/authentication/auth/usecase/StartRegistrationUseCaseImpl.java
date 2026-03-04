package org.mangala.authentication.auth.usecase;

import com.webauthn4j.data.*;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import lombok.RequiredArgsConstructor;
import org.mangala.authentication.auth.usecase.model.StartRegisterPasskeyResponse;
import org.mangala.authentication.passkey.domain.PasskeyChallengeOperationType;
import org.mangala.authentication.passkey.usecase.CreatePasskeyChallengeUseCase;
import org.mangala.authentication.passkey.usecase.command.CreatePasskeyChallengeCommand;
import org.mangala.authentication.shared.config.WebAuthnConfigProperties;
import org.mangala.authentication.user.domain.UserWithEmailExistedException;
import org.mangala.authentication.user.usecase.CheckUserExistedUseCase;
import org.mangala.authentication.user.usecase.CreateUserUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StartRegistrationUseCaseImpl implements StartRegistrationUseCase {

    private final WebAuthnConfigProperties webAuthnConfigProperties;
    private final CheckUserExistedUseCase checkUserExistedUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final CreatePasskeyChallengeUseCase createPasskeyChallengeUseCase;

    @Override
    public StartRegisterPasskeyResponse execute(String email, String displayName, String ipAddress, String userAgent) {
        final var sessionId = UUID.randomUUID().toString();
        final var challenge = new DefaultChallenge().getValue();
        System.out.println("email before normalized: " + email);
        final var normalizedEmail = normalizeEmail(email);
        System.out.println("email from auth: " + normalizedEmail);
        final var rp = new PublicKeyCredentialRpEntity(
                webAuthnConfigProperties.getRp().getId(),
                webAuthnConfigProperties.getRp().getName()
        );
        String username;
        String userDisplayName;
        if (Objects.nonNull(normalizedEmail)) {
            if (checkUserExistedUseCase.execute(normalizedEmail)) {
                throw new UserWithEmailExistedException();
            }
            username = normalizedEmail;
            userDisplayName = Objects.nonNull(displayName) && !displayName.isBlank() ? displayName : normalizedEmail;
        } else {
            username = UUID.randomUUID().toString();
            userDisplayName = Objects.nonNull(displayName) && !displayName.isBlank() ? displayName : username;
        }

        final var persistedUser = createUserUseCase.execute(normalizedEmail, null);
        final var userEntity = new PublicKeyCredentialUserEntity(
                persistedUser.getId().toString().getBytes(StandardCharsets.UTF_8),
                username,
                userDisplayName
        );

        createPasskeyChallenge(challenge, sessionId, ipAddress, userAgent, persistedUser.getId());

        AuthenticatorSelectionCriteria authenticatorSelection =
                new AuthenticatorSelectionCriteria(
                        webAuthnConfigProperties.getAuthenticatorAttachment(),
                        webAuthnConfigProperties.getAuthenticator().getResidentKey(),
                        webAuthnConfigProperties.getUserVerificationRequirement()
                );

        return StartRegisterPasskeyResponse.builder()
                .sessionId(sessionId)
                .rp(rp)
                .user(userEntity)
                .challenge(challenge)
                .publicKeyCredentialParameters(webAuthnConfigProperties.getPubKeyCredParams())
                .timeout(webAuthnConfigProperties.getTimeout())
                .excludeCredentials(Collections.emptyList())
                .attestation(webAuthnConfigProperties.getAttestationConveyancePreference())
                .authenticatorSelectionCriteria(authenticatorSelection)
                .build();
    }

    private void createPasskeyChallenge(
            byte[] challenge,
            String session,
            String ipAddress,
            String userAgent,
            UUID userId
    ) {
        final var expiresAt = LocalDateTime.now().plusSeconds(webAuthnConfigProperties.getTimeout());
        var createPasskeyChallengeCommand = CreatePasskeyChallengeCommand
                .builder()
                .operationType(PasskeyChallengeOperationType.REGISTER)
                .sessionId(session)
                .challenge(challenge)
                .userId(userId.toString())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiresAt(expiresAt)
                .userVerificationRequirement(webAuthnConfigProperties.getAuthenticator().getUserVerification())
                .build();
        createPasskeyChallengeUseCase.execute(createPasskeyChallengeCommand);
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        String trimmed = email.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
