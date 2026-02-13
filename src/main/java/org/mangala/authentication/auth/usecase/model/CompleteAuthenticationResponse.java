package org.mangala.authentication.auth.usecase.model;

import java.util.UUID;

public record CompleteAuthenticationResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UUID userId,
        String email
) {
}
