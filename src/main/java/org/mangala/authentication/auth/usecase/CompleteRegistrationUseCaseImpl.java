package org.mangala.authentication.auth.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mangala.authentication.auth.usecase.command.CompletePasskeyRegistrationCommand;
import org.mangala.authentication.auth.usecase.model.CompletePasskeyRegistrationResponse;
import org.mangala.authentication.passkey.domain.ChallengeAlreadyUsedException;
import org.mangala.authentication.passkey.domain.ChallengeExpiredException;
import org.mangala.authentication.passkey.domain.PasskeyChallengeEntity;
import org.mangala.authentication.passkey.usecase.*;
import org.mangala.authentication.passkey.usecase.command.SavePasskeyCommand;
import org.mangala.authentication.passkey.usecase.command.VerifyRegistrationCredentialCommand;
import org.mangala.authentication.user.adapter.repository.UserRepository;
import org.mangala.authentication.user.domain.UserEntity;
import org.mangala.authentication.user.domain.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompleteRegistrationUseCaseImpl implements CompleteRegistrationUseCase {

    private final GetPasskeyChallengeUseCase getPasskeyChallengeUseCase;
    private final VerifyRegistrationCredentialUseCase verifyCredentialUseCase;
    private final CheckDuplicateCredentialUseCase checkDuplicateCredentialUseCase;
    private final SavePasskeyUseCase savePasskeyUseCase;
    private final MarkChallengeAsUsedUseCase markChallengeAsUsedUseCase;
    private final UserRepository userRepository;

    @Override
    public CompletePasskeyRegistrationResponse execute(CompletePasskeyRegistrationCommand command) {
        log.info("Starting passkey registration completion for sessionId: {}", command.getSessionId());

        // 1. Retrieve and validate challenge
        PasskeyChallengeEntity challenge = getPasskeyChallengeUseCase.execute(command.getSessionId());
        validateChallenge(challenge);

        // 2. Verify credential using WebAuthn4J
        var verifyCommand = VerifyRegistrationCredentialCommand.builder()
            .challenge(challenge.getChallenge())
            .clientDataJSON(command.getClientDataJSON())
            .attestationObject(command.getAttestationObject())
            .ipAddress(command.getIpAddress())
            .build();

        var verificationResult = verifyCredentialUseCase.execute(verifyCommand);
        log.debug("Credential verified successfully. UserVerified: {}, BackupEligible: {}",
            verificationResult.getUserVerified(), verificationResult.getBackupEligible());

        // 3. Check for duplicate credential
        checkDuplicateCredentialUseCase.execute(command.getCredentialId());

        // 4. Create or get user
        UserEntity user = getUserForChallenge(challenge);
        log.debug("User determined: {}", user.getId());

        // 5. Save passkey
        var saveCommand = SavePasskeyCommand.builder()
            .userId(user.getId())
            .credentialId(command.getCredentialId())
            .publicKey(verificationResult.getPublicKey())
            .algorithm(verificationResult.getAlgorithm())
            .signCount(verificationResult.getSignCount())
            .aaguid(verificationResult.getAaguid())
            .transports(command.getTransports())
            .backupEligible(verificationResult.getBackupEligible())
            .backupState(verificationResult.getBackupState())
            .userVerified(verificationResult.getUserVerified())
            .attestationFormat(verificationResult.getAttestationFormat())
            .attestationCertificate(verificationResult.getAttestationCertificate())
            .deviceName(extractDeviceName(command.getUserAgent()))
            .build();

        var passkey = savePasskeyUseCase.execute(saveCommand);
        log.info("Passkey saved successfully for user: {}", user.getId());

        // 6. Mark challenge as used
        markChallengeAsUsedUseCase.execute(challenge.getId());

        // 7. Build response
        return CompletePasskeyRegistrationResponse.builder()
            .userId(user.getId())
            .credentialId(command.getCredentialIdBase64Url())
            .userVerified(passkey.getUserVerified())
            .backupEligible(passkey.getBackupEligible())
            .backupState(passkey.getBackupState())
            .deviceName(passkey.getDeviceName())
            .createdAt(passkey.getCreatedAt())
            .build();
    }

    private void validateChallenge(PasskeyChallengeEntity challenge) {
        if (challenge.getIsUsed()) {
            log.warn("Challenge already used: {}", challenge.getSessionId());
            throw new ChallengeAlreadyUsedException();
        }

        if (challenge.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Challenge expired: {}", challenge.getSessionId());
            throw new ChallengeExpiredException();
        }
    }

    private UserEntity getUserForChallenge(PasskeyChallengeEntity challenge) {
        if (challenge.getUserId() == null) {
            log.error("Registration challenge {} has no userId", challenge.getSessionId());
            throw new UserNotFoundException();
        }

        return userRepository.findById(challenge.getUserId())
            .orElseThrow(UserNotFoundException::new);
    }

    private String extractDeviceName(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown Device";
        }

        // Simple device name extraction from User-Agent
        // Can be enhanced with ua-parser library for better parsing
        String deviceName = userAgent;

        // Truncate if too long
        if (deviceName.length() > 255) {
            deviceName = deviceName.substring(0, 255);
        }

        return deviceName;
    }
}
