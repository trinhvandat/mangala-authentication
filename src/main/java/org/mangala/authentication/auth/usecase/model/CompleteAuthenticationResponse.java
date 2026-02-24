package org.mangala.authentication.auth.usecase.model;

import java.util.UUID;

public record CompleteAuthenticationResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        long refreshExpiresIn,
        UUID userId,
        String email
) {
}
