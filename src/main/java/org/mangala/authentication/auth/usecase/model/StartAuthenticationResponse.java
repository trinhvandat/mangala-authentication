package org.mangala.authentication.auth.usecase.model;

import java.util.List;

public record StartAuthenticationResponse(
        String challenge,
        String rpId,
        List<AllowedCredential> allowCredentials,
        String userVerification,
        long timeout
) {
    public record AllowedCredential(
            String id,
            String type
    ) {
    }
}
