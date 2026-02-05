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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StartRegistrationUseCaseImpl implements StartRegistrationUseCase {

    private final WebAuthnConfigProperties webAuthnConfigProperties;
    private final CheckUserExistedUseCase checkUserExistedUseCase;
    private final CreatePasskeyChallengeUseCase createPasskeyChallengeUseCase;

    @Override
    public StartRegisterPasskeyResponse execute(String email, String ipAddress, String userAgent) {
        final var sessionId = UUID.randomUUID().toString();
        final var challenge = new DefaultChallenge().getValue();
        final var rp = new PublicKeyCredentialRpEntity(
                webAuthnConfigProperties.getRp().getId(),
                webAuthnConfigProperties.getRp().getName()
        );
        String username;
        String userDisplayName;
        if (Objects.nonNull(email)) {
            if (checkUserExistedUseCase.execute(email)) {
                throw new UserWithEmailExistedException();
            }
            username = email;
            userDisplayName = email;
        } else {
            username = UUID.randomUUID().toString();
            userDisplayName = username;
        }
        final var userEntity = new PublicKeyCredentialUserEntity(
                UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8),
                username,
                userDisplayName
        );

        createPasskeyChallenge(challenge, sessionId, ipAddress, userAgent);

        AuthenticatorSelectionCriteria authenticatorSelection =
                new AuthenticatorSelectionCriteria(
                        webAuthnConfigProperties.getAuthenticatorAttachment(),
                        webAuthnConfigProperties.getAuthenticator().getResidentKey(),
                        webAuthnConfigProperties.getUserVerificationRequirement()
                );

        return StartRegisterPasskeyResponse.builder()
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

    private void createPasskeyChallenge(byte[] challenge, String session, String ipAddress, String userAgent) {
        final var expiresAt = LocalDateTime.now().plusSeconds(webAuthnConfigProperties.getTimeout());
        var createPasskeyChallengeCommand = CreatePasskeyChallengeCommand
                .builder()
                .operationType(PasskeyChallengeOperationType.REGISTER)
                .sessionId(session)
                .challenge(challenge)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiresAt(expiresAt)
                .userVerificationRequirement(webAuthnConfigProperties.getAuthenticator().getUserVerification())
                .build();
        createPasskeyChallengeUseCase.execute(createPasskeyChallengeCommand);
    }
}
