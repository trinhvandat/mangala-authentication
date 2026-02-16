package org.mangala.authentication.auth.usecase;

import com.webauthn4j.util.Base64UrlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mangala.authentication.auth.usecase.command.CompleteAuthenticationCommand;
import org.mangala.authentication.auth.usecase.model.CompleteAuthenticationResponse;
import org.mangala.authentication.passkey.domain.InvalidChallengeTypeException;
import org.mangala.authentication.passkey.domain.PasskeyChallengeEntity;
import org.mangala.authentication.passkey.domain.PasskeyChallengeOperationType;
import org.mangala.authentication.passkey.domain.UserPasskeyEntity;
import org.mangala.authentication.passkey.usecase.GetPasskeyByCredentialIdUseCase;
import org.mangala.authentication.passkey.usecase.UpdatePasskeySignCountUseCase;
import org.mangala.authentication.passkey.usecase.VerifyAuthenticationAssertionUseCase;
import org.mangala.authentication.passkey.usecase.command.UpdatePasskeySignCountCommand;
import org.mangala.authentication.passkey.usecase.command.VerifyAuthenticationAssertionCommand;
import org.mangala.authentication.passkey.usecase.model.VerifyAuthenticationAssertionResponse;
import org.mangala.authentication.shared.config.WebAuthnConfigProperties;
import org.mangala.authentication.passkey.domain.ChallengeNotFoundException;
import org.mangala.authentication.user.domain.UserEntity;
import org.mangala.authentication.user.domain.UserNotFoundException;
import org.mangala.authentication.user.adapter.repository.UserRepository;
import org.mangala.authentication.passkey.adapter.repository.PasskeyChallengeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompleteAuthenticationUseCaseImpl implements CompleteAuthenticationUseCase {

    private final PasskeyChallengeRepository passkeyChallengeRepository;
    private final GetPasskeyByCredentialIdUseCase getPasskeyByCredentialIdUseCase;
    private final VerifyAuthenticationAssertionUseCase verifyAuthenticationAssertionUseCase;
    private final UpdatePasskeySignCountUseCase updatePasskeySignCountUseCase;
    private final UserRepository userRepository;
    private final WebAuthnConfigProperties webAuthnConfig;

    @Override
    @Transactional
    public CompleteAuthenticationResponse execute(CompleteAuthenticationCommand command) {
        log.info("Completing authentication for credential: {}", command.credentialId());

        // Decode Base64URL values
        byte[] credentialId = Base64UrlUtil.decode(command.credentialId());
        byte[] authenticatorData = Base64UrlUtil.decode(command.authenticatorData());
        byte[] clientDataJSON = Base64UrlUtil.decode(command.clientDataJSON());
        byte[] signature = Base64UrlUtil.decode(command.signature());
        byte[] userHandle = command.userHandle() != null ? Base64UrlUtil.decode(command.userHandle()) : null;

        // Get passkey by credential ID
        UserPasskeyEntity passkey = getPasskeyByCredentialIdUseCase.execute(credentialId);

        // Get the challenge
        PasskeyChallengeEntity challenge = passkeyChallengeRepository
                .findTopByUserIdAndIsUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        passkey.getUserId(),
                        LocalDateTime.now()
                )
                .orElseThrow(ChallengeNotFoundException::new);

        // Validate challenge type
        if (challenge.getOperationType() != PasskeyChallengeOperationType.AUTHENTICATE) {
            throw new InvalidChallengeTypeException();
        }

        // Verify the assertion
        VerifyAuthenticationAssertionCommand verifyCommand = new VerifyAuthenticationAssertionCommand(
                credentialId,
                authenticatorData,
                clientDataJSON,
                signature,
                userHandle,
                passkey.getPublicKey(),
                Base64UrlUtil.encodeToString(challenge.getChallenge()),
                webAuthnConfig.getRp().getId(),
                webAuthnConfig.getRp().getOrigin(),
                true // require user verification
        );

        VerifyAuthenticationAssertionResponse verifyResponse = verifyAuthenticationAssertionUseCase.execute(verifyCommand);

        // Update sign count
        UpdatePasskeySignCountCommand updateSignCountCommand = new UpdatePasskeySignCountCommand(
                credentialId,
                verifyResponse.signCount()
        );
        updatePasskeySignCountUseCase.execute(updateSignCountCommand);

        // Mark challenge as used
        challenge.setIsUsed(true);
        passkeyChallengeRepository.save(challenge);

        // Update last used time
        passkey.setLastUsedAt(LocalDateTime.now());

        // Get user
        UserEntity user = userRepository.findById(passkey.getUserId())
                .orElseThrow(UserNotFoundException::new);

        // TODO: Generate JWT tokens using common-security module
        // For now, return placeholder values
        String accessToken = generatePlaceholderToken(user.getId(), "access");
        String refreshToken = generatePlaceholderToken(user.getId(), "refresh");

        log.info("Authentication completed successfully for user: {}", user.getEmail());

        return new CompleteAuthenticationResponse(
                accessToken,
                refreshToken,
                "Bearer",
                3600L, // 1 hour
                user.getId(),
                user.getEmail()
        );
    }

    private String generatePlaceholderToken(UUID userId, String type) {
        // TODO: Replace with actual JWT generation from common-security module
        return "placeholder_" + type + "_token_for_" + userId;
    }
}
