package org.mangala.authentication.shared.config;

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.util.ObjectConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebAuthn4J Configuration
 *
 * Configures the WebAuthn4J library for passkey credential verification.
 * Uses the non-strict WebAuthnManager which is suitable for passkey authentication
 * where attestation is typically "none".
 */
@Configuration
public class WebAuthn4JConfig {

    /**
     * ObjectConverter for JSON serialization/deserialization
     */
    @Bean
    public ObjectConverter objectConverter() {
        return new ObjectConverter();
    }

    /**
     * WebAuthnManager - Main entry point for WebAuthn operations
     *
     * Using non-strict mode which accepts attestations without strict verification.
     * This is suitable for passkey-based authentication where attestation is typically "none".
     *
     * For production with hardware security keys requiring attestation verification,
     * use WebAuthnManager.createStrictWebAuthnManager() with proper trust anchors.
     */
    @Bean
    public WebAuthnManager webAuthnManager(ObjectConverter objectConverter) {
        return WebAuthnManager.createNonStrictWebAuthnManager(objectConverter);
    }
}

