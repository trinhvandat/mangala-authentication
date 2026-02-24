package org.mangala.authentication.shared.config;

import com.webauthn4j.data.*;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "application.authentication.passkey")
@Data
public class WebAuthnConfigProperties {
    private RelyingParty rp;
    private Long timeout;
    private Authenticator authenticator = new Authenticator();
    private String attestation = "none";
    private List<String> algorithms = List.of("ES256", "RS256");


    @Data
    @NoArgsConstructor
    public static class RelyingParty {
        private String id;
        private String name;
        private String origin; // e.g., "https://example.com" - required for WebAuthn verification
    }

    @Data
    public static class Authenticator {
        private String attachment; // platform, cross-platform, null
        private Boolean residentKey = true;
        /**
         * This field is required user authentication for some value below:
         * - preferred: server want user to have device support to fingerprint or face scanning, but not required. - default
         * - required: server require user have to unlock pin, fingerprint or biometric.
         * - discouraged: server does not care about user authentication - should not use.
         */
        private String userVerification = "preferred";
    }

    public AuthenticatorAttachment getAuthenticatorAttachment() {
        if (authenticator.attachment == null || "both".equalsIgnoreCase(authenticator.attachment)) {
            return null; // Cho phép cả platform và cross-platform
        }
        return switch (authenticator.attachment.toLowerCase()) {
            case "platform" -> AuthenticatorAttachment.PLATFORM;
            case "cross-platform" -> AuthenticatorAttachment.CROSS_PLATFORM;
            default -> null;
        };
    }

    public UserVerificationRequirement getUserVerificationRequirement() {
        return switch (authenticator.userVerification.toLowerCase()) {
            case "required" -> UserVerificationRequirement.REQUIRED;
            case "discouraged" -> UserVerificationRequirement.DISCOURAGED;
            default -> UserVerificationRequirement.PREFERRED;
        };
    }

    public AttestationConveyancePreference getAttestationConveyancePreference() {
        return switch (attestation.toLowerCase()) {
            case "direct" -> AttestationConveyancePreference.DIRECT;
            case "indirect" -> AttestationConveyancePreference.INDIRECT;
            case "enterprise" -> AttestationConveyancePreference.ENTERPRISE;
            default -> AttestationConveyancePreference.NONE;
        };
    }

    public List<PublicKeyCredentialParameters> getPubKeyCredParams() {
        return algorithms.stream()
                .map(this::algorithmToCredentialParam)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    private PublicKeyCredentialParameters algorithmToCredentialParam(String algorithm) {
        COSEAlgorithmIdentifier coseAlg = switch (algorithm.toUpperCase()) {
            case "ES256" -> COSEAlgorithmIdentifier.ES256;     // -7
            case "ES384" -> COSEAlgorithmIdentifier.ES384;     // -35
            case "ES512" -> COSEAlgorithmIdentifier.ES512;     // -36
            case "RS256" -> COSEAlgorithmIdentifier.RS256;     // -257
            case "RS384" -> COSEAlgorithmIdentifier.RS384;     // -258
            case "RS512" -> COSEAlgorithmIdentifier.RS512;     // -259
            case "PS256" -> COSEAlgorithmIdentifier.PS256;     // -37
            case "PS384" -> COSEAlgorithmIdentifier.PS384;     // -38
            case "PS512" -> COSEAlgorithmIdentifier.PS512;     // -39
            case "EDDSA" -> COSEAlgorithmIdentifier.EdDSA;     // -8
            default -> null;
        };

        if (coseAlg == null) {
            return null;
        }

        return new PublicKeyCredentialParameters(
                PublicKeyCredentialType.PUBLIC_KEY,
                coseAlg
        );
    }
}
