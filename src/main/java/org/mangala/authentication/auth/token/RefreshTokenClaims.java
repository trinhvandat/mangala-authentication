package org.mangala.authentication.auth.token;

import java.util.UUID;

public record RefreshTokenClaims(
        UUID userId,
        String tokenId,
        String email
) {
}
