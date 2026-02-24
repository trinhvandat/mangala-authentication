package org.mangala.authentication.passkey.usecase;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.RegistrationParameters;
import com.webauthn4j.data.RegistrationRequest;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mangala.authentication.passkey.domain.CredentialVerificationFailedException;
import org.mangala.authentication.passkey.usecase.command.VerifyRegistrationCredentialCommand;
import org.mangala.authentication.passkey.usecase.model.VerifiedCredentialResult;
import org.mangala.authentication.shared.config.WebAuthnConfigProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyRegistrationCredentialUseCaseImpl implements VerifyRegistrationCredentialUseCase {

    private final WebAuthnManager webAuthnManager;
    private final ObjectConverter objectConverter;
    private final WebAuthnConfigProperties config;

    @Override
    public VerifiedCredentialResult execute(VerifyRegistrationCredentialCommand command) {
        try {
            log.debug("Starting credential verification for registration");

            // 1. Create Challenge
            Challenge challenge = new DefaultChallenge(command.getChallenge());

            // 2. Create Server Property
            ServerProperty serverProperty = new ServerProperty(
                new Origin(config.getRp().getOrigin()),
                config.getRp().getId(),
                challenge,
                null // tokenBindingId
            );

            // 3. Create Registration Request (what the client sent)
            RegistrationRequest registrationRequest = new RegistrationRequest(
                command.getAttestationObject(),
                command.getClientDataJSON()
            );

            // 4. Create Registration Parameters (server expectations)
            RegistrationParameters registrationParameters = new RegistrationParameters(
                serverProperty,
                null, // pubKeyCredParams - not needed for verification
                config.getUserVerificationRequirement().equals(
                    com.webauthn4j.data.UserVerificationRequirement.REQUIRED
                ),
                true // userPresenceRequired
            );

            // 5. Verify the registration
            var registrationData = webAuthnManager.verify(
                registrationRequest,
                registrationParameters
            );

            // 6. Extract verified data
            var authenticatorData = registrationData.getAttestationObject()
                .getAuthenticatorData();
            var attestedCredentialData = authenticatorData.getAttestedCredentialData();
            var coseKey = attestedCredentialData.getCOSEKey();

            log.debug("Credential verification successful. Algorithm: {}, UserVerified: {}",
                coseKey.getAlgorithm(), authenticatorData.isFlagUV());

            // 7. Build result
            // Serialize the COSE key to bytes
            byte[] publicKeyBytes = objectConverter.getCborConverter()
                .writeValueAsBytes(coseKey);

            return VerifiedCredentialResult.builder()
                .publicKey(publicKeyBytes)
                .algorithm((int) coseKey.getAlgorithm().getValue())
                .credentialId(attestedCredentialData.getCredentialId())
                .aaguid(attestedCredentialData.getAaguid().getBytes())
                .signCount(authenticatorData.getSignCount())
                .userVerified(authenticatorData.isFlagUV())
                .userPresent(authenticatorData.isFlagUP())
                .backupEligible(authenticatorData.isFlagBE())
                .backupState(authenticatorData.isFlagBS())
                .attestationFormat(registrationData.getAttestationObject()
                    .getFormat())
                .attestationCertificate(null) // Simplified - not extracting certificate
                .build();

        } catch (DataConversionException e) {
            log.error("Failed to parse credential data", e);
            throw new CredentialVerificationFailedException();
        } catch (Exception e) {
            log.error("Credential verification failed", e);
            throw new CredentialVerificationFailedException();
        }
    }
}
