package org.mangala.authentication.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mangala.authentication.auth.domain.InvalidTokenException;
import org.mangala.authentication.shared.config.security.JwtProperties;
import org.mangala.security.SecurityConstants;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("test-secret-key-with-minimum-32-bytes-for-hmac-12345");
        jwtProperties.setIssuer("mangala");
        jwtProperties.setAccessTokenAudience("mangala-gateway");
        jwtProperties.setRefreshTokenAudience("mangala-auth-refresh");
        jwtProperties.setAccessTokenExpirationSeconds(900);
        jwtProperties.setRefreshTokenExpirationSeconds(604800);
        jwtTokenService = new JwtTokenService(jwtProperties);
    }

    @Test
    void shouldIssueValidAccessAndRefreshTokenPair() {
        UUID userId = UUID.randomUUID();

        JwtTokenBundle bundle = jwtTokenService.issueTokenPair(
                userId,
                "user@example.com",
                List.of("ROLE_USER"),
                List.of("wallets:read")
        );

        Claims accessClaims = parseClaims(bundle.accessToken());

        assertEquals(userId.toString(), accessClaims.getSubject());
        assertEquals("mangala", accessClaims.getIssuer());
        assertEquals(SecurityConstants.TOKEN_TYPE_ACCESS, accessClaims.get(SecurityConstants.CLAIM_TOKEN_TYPE, String.class));
        assertEquals("user@example.com", accessClaims.get(SecurityConstants.CLAIM_EMAIL, String.class));

        @SuppressWarnings("unchecked")
        List<String> roles = accessClaims.get(SecurityConstants.CLAIM_ROLES, List.class);
        assertTrue(roles.contains("ROLE_USER"));

        RefreshTokenClaims refreshTokenClaims = jwtTokenService.parseAndValidateRefreshToken(bundle.refreshToken());
        assertEquals(userId, refreshTokenClaims.userId());
        assertEquals(bundle.refreshTokenId(), refreshTokenClaims.tokenId());
        assertEquals("user@example.com", refreshTokenClaims.email());
    }

    @Test
    void shouldRejectAccessTokenWhenUsedAsRefreshToken() {
        JwtTokenBundle bundle = jwtTokenService.issueTokenPair(
                UUID.randomUUID(),
                "user@example.com",
                List.of("ROLE_USER"),
                List.of()
        );

        assertThrows(InvalidTokenException.class, () -> jwtTokenService.parseAndValidateRefreshToken(bundle.accessToken()));
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)))
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
