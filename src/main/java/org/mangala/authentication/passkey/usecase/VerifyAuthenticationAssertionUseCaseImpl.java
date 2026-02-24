package org.mangala.authentication.passkey.usecase;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticationRequest;
import com.webauthn4j.data.attestation.authenticator.AAGUID;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.authenticator.COSEKey;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.util.Base64UrlUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mangala.authentication.passkey.domain.AssertionVerificationFailedException;
import org.mangala.authentication.passkey.usecase.command.VerifyAuthenticationAssertionCommand;
import org.mangala.authentication.passkey.usecase.model.VerifyAuthenticationAssertionResponse;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyAuthenticationAssertionUseCaseImpl implements VerifyAuthenticationAssertionUseCase {

    private final WebAuthnManager webAuthnManager;
    private final ObjectConverter objectConverter;

    @Override
    public VerifyAuthenticationAssertionResponse execute(VerifyAuthenticationAssertionCommand command) {
        try {
            // Create authentication request from client data
            AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                    command.credentialId(),
                    command.userHandle(),
                    command.authenticatorData(),
                    command.clientDataJSON(),
                    command.signature()
            );

            // Create server property
            ServerProperty serverProperty = new ServerProperty(
                    Origin.create(command.origin()),
                    command.rpId(),
                    new DefaultChallenge(Base64UrlUtil.decode(command.expectedChallenge())),
                    null // tokenBindingId is optional
            );

            // Deserialize the stored public key from CBOR bytes
            COSEKey coseKey = objectConverter.getCborConverter()
                    .readValue(command.storedPublicKey(), COSEKey.class);

            // Create AttestedCredentialData with the credential ID and public key
            AAGUID aaguid = new AAGUID(new byte[16]); // aaguid (not needed for authentication)
            AttestedCredentialData attestedCredentialData = new AttestedCredentialData(
                    aaguid,
                    command.credentialId(),
                    coseKey
            );

            // Create authenticator with the attested credential data
            Authenticator authenticator = new AuthenticatorImpl(
                    attestedCredentialData,
                    null, // attestationStatement is not needed for authentication
                    0L    // signCount will be updated after validation
            );

            // Create authentication parameters
            AuthenticationParameters authenticationParameters = new AuthenticationParameters(
                    serverProperty,
                    authenticator,
                    null, // allowCredentials is optional for this validation
                    command.userVerificationRequired()
            );

            // Parse and validate the authentication data
            AuthenticationData authenticationData = webAuthnManager.parse(authenticationRequest);
            webAuthnManager.validate(authenticationData, authenticationParameters);

            // Extract results from authenticator data flags
            byte flagsByte = authenticationData.getAuthenticatorData().getFlags();
            boolean userVerified = (flagsByte & 0x04) != 0; // UV flag is bit 2

            long signCount = authenticationData.getAuthenticatorData().getSignCount();

            log.info("Authentication assertion verified successfully. User verified: {}, Sign count: {}",
                    userVerified, signCount);

            return new VerifyAuthenticationAssertionResponse(userVerified, signCount);

        } catch (Exception e) {
            log.error("Authentication assertion verification failed", e);
            throw new AssertionVerificationFailedException();
        }
    }
}
