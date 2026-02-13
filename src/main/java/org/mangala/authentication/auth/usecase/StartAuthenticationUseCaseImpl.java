package org.mangala.authentication.auth.usecase;

import com.webauthn4j.data.UserVerificationRequirement;
import com.webauthn4j.util.Base64UrlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mangala.authentication.auth.usecase.command.StartAuthenticationCommand;
import org.mangala.authentication.auth.usecase.model.StartAuthenticationResponse;
import org.mangala.authentication.passkey.domain.NoPasskeysFoundException;
import org.mangala.authentication.passkey.domain.PasskeyChallengeEntity;
import org.mangala.authentication.passkey.domain.PasskeyChallengeOperationType;
import org.mangala.authentication.passkey.domain.UserPasskeyEntity;
import org.mangala.authentication.passkey.usecase.CreatePasskeyChallengeUseCase;
import org.mangala.authentication.passkey.usecase.GetUserPasskeysUseCase;
import org.mangala.authentication.passkey.usecase.command.CreatePasskeyChallengeCommand;
import org.mangala.authentication.shared.config.WebAuthnConfigProperties;
import org.mangala.authentication.user.domain.UserEntity;
import org.mangala.authentication.user.domain.UserNotFoundException;
import org.mangala.authentication.user.adapter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StartAuthenticationUseCaseImpl implements StartAuthenticationUseCase {

    private static final int CHALLENGE_LENGTH = 32;
    private static final long TIMEOUT_MS = 300000; // 5 minutes

    private final UserRepository userRepository;
    private final GetUserPasskeysUseCase getUserPasskeysUseCase;
    private final CreatePasskeyChallengeUseCase createPasskeyChallengeUseCase;
    private final WebAuthnConfigProperties webAuthnConfig;
    private final SecureRandom secureRandom;

    @Override
    public StartAuthenticationResponse execute(StartAuthenticationCommand command) {
        log.info("Starting authentication for user: {}", command.email());

        // Find user by email
        UserEntity user = userRepository.findByEmail(command.email())
                .orElseThrow(UserNotFoundException::new);

        // Get user's active passkeys
        List<UserPasskeyEntity> passkeys = getUserPasskeysUseCase.execute(user.getId());
        if (passkeys.isEmpty()) {
            throw new NoPasskeysFoundException();
        }

        // Generate random challenge
        byte[] challengeBytes = new byte[CHALLENGE_LENGTH];
        secureRandom.nextBytes(challengeBytes);
        String challenge = Base64UrlUtil.encodeToString(challengeBytes);
        String sessionId = UUID.randomUUID().toString();

        // Create challenge record
        CreatePasskeyChallengeCommand challengeCommand = CreatePasskeyChallengeCommand.builder()
                .challenge(challengeBytes)
                .userId(user.getId().toString())
                .operationType(PasskeyChallengeOperationType.AUTHENTICATE)
                .sessionId(sessionId)
                .ipAddress(command.ipAddress())
                .userAgent(command.userAgent())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .userVerificationRequirement(UserVerificationRequirement.REQUIRED.getValue())
                .build();
        createPasskeyChallengeUseCase.execute(challengeCommand);

        // Map passkeys to allowed credentials
        List<StartAuthenticationResponse.AllowedCredential> allowedCredentials = passkeys.stream()
                .map(passkey -> new StartAuthenticationResponse.AllowedCredential(
                        Base64UrlUtil.encodeToString(passkey.getCredentialId()),
                        "public-key"
                ))
                .collect(Collectors.toList());

        log.info("Authentication started successfully for user: {}. Allowed credentials: {}",
                command.email(), allowedCredentials.size());

        return new StartAuthenticationResponse(
                challenge,
                webAuthnConfig.getRp().getId(),
                allowedCredentials,
                UserVerificationRequirement.REQUIRED.getValue(),
                TIMEOUT_MS
        );
    }
}
