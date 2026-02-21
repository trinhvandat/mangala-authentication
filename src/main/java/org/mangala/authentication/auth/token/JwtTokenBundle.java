package org.mangala.authentication.auth.token;

import java.time.Instant;

public record JwtTokenBundle(
        String accessToken,
        String refreshToken,
        String refreshTokenId,
        long accessExpiresInSeconds,
        Instant refreshExpiresAt
) {
}
