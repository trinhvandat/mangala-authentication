package org.mangala.authentication.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.mangala.authentication.auth.domain.InvalidTokenException;
import org.mangala.authentication.shared.config.security.JwtProperties;
import org.mangala.security.SecurityConstants;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private static final String CLAIM_AUTH_METHODS = "amr";

    private final JwtProperties jwtProperties;

    public JwtTokenBundle issueTokenPair(UUID userId, String email, Collection<String> roles, Collection<String> permissions) {
        Instant now = Instant.now();
        Instant accessExpiry = now.plusSeconds(jwtProperties.getAccessTokenExpirationSeconds());
        Instant refreshExpiry = now.plusSeconds(jwtProperties.getRefreshTokenExpirationSeconds());

        String accessJti = UUID.randomUUID().toString();
        String refreshJti = UUID.randomUUID().toString();

        Map<String, Object> accessClaims = new HashMap<>();
        if (email != null) {
            accessClaims.put(SecurityConstants.CLAIM_EMAIL, email);
        }
        accessClaims.put(SecurityConstants.CLAIM_ROLES, new ArrayList<>(roles));
        accessClaims.put(SecurityConstants.CLAIM_PERMISSIONS, new ArrayList<>(permissions));
        accessClaims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_ACCESS);
        accessClaims.put(CLAIM_AUTH_METHODS, List.of("passkey"));

        String accessToken = buildToken(
                userId,
                jwtProperties.getAccessTokenAudience(),
                accessJti,
                now,
                accessExpiry,
                accessClaims
        );

        Map<String, Object> refreshClaims = new HashMap<>();
        if (email != null) {
            refreshClaims.put(SecurityConstants.CLAIM_EMAIL, email);
        }
        refreshClaims.put(SecurityConstants.CLAIM_TOKEN_TYPE, SecurityConstants.TOKEN_TYPE_REFRESH);

        String refreshToken = buildToken(
                userId,
                jwtProperties.getRefreshTokenAudience(),
                refreshJti,
                now,
                refreshExpiry,
                refreshClaims
        );

        return new JwtTokenBundle(
                accessToken,
                refreshToken,
                refreshJti,
                jwtProperties.getAccessTokenExpirationSeconds(),
                jwtProperties.getRefreshTokenExpirationSeconds(),
                refreshExpiry
        );
    }

    public RefreshTokenClaims parseAndValidateRefreshToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);

        String tokenType = claims.get(SecurityConstants.CLAIM_TOKEN_TYPE, String.class);
        if (!SecurityConstants.TOKEN_TYPE_REFRESH.equals(tokenType)) {
            throw new InvalidTokenException();
        }

        Set<String> audiences = claims.getAudience();
        if (audiences == null || !audiences.contains(jwtProperties.getRefreshTokenAudience())) {
            throw new InvalidTokenException();
        }

        String subject = claims.getSubject();
        String tokenId = claims.getId();
        String email = claims.get(SecurityConstants.CLAIM_EMAIL, String.class);

        return new RefreshTokenClaims(UUID.fromString(subject), tokenId, email);
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.getAccessTokenExpirationSeconds();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .requireIssuer(jwtProperties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidTokenException();
        }
    }

    private String buildToken(
            UUID userId,
            String audience,
            String jti,
            Instant issuedAt,
            Instant expiresAt,
            Map<String, Object> claims
    ) {
        return Jwts.builder()
                .claims(claims)
                .subject(userId.toString())
                .issuer(jwtProperties.getIssuer())
                .audience().add(audience).and()
                .id(jti)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
