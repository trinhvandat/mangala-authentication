package org.mangala.authentication.auth.usecase;

import lombok.RequiredArgsConstructor;
import org.mangala.authentication.auth.adapter.repository.RefreshTokenRepository;
import org.mangala.authentication.auth.adapter.repository.UserAuthorizationQueryRepository;
import org.mangala.authentication.auth.domain.RefreshTokenEntity;
import org.mangala.authentication.auth.domain.RefreshTokenNotFoundException;
import org.mangala.authentication.auth.domain.RefreshTokenRevokedException;
import org.mangala.authentication.auth.token.JwtTokenBundle;
import org.mangala.authentication.auth.token.JwtTokenService;
import org.mangala.authentication.auth.token.RefreshTokenClaims;
import org.mangala.authentication.auth.usecase.command.RefreshTokenCommand;
import org.mangala.authentication.auth.usecase.model.CompleteAuthenticationResponse;
import org.mangala.authentication.user.adapter.repository.UserRepository;
import org.mangala.authentication.user.domain.UserEntity;
import org.mangala.authentication.user.domain.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final JwtTokenService jwtTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final UserAuthorizationQueryRepository authorizationQueryRepository;

    @Override
    @Transactional
    public CompleteAuthenticationResponse execute(RefreshTokenCommand command) {
        RefreshTokenClaims claims = jwtTokenService.parseAndValidateRefreshToken(command.refreshToken());

        RefreshTokenEntity currentToken = refreshTokenRepository.findByTokenId(claims.tokenId())
                .orElseThrow(RefreshTokenNotFoundException::new);

        if (currentToken.getRevokedAt() != null) {
            throw new RefreshTokenRevokedException();
        }

        if (currentToken.getExpiresAt().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            currentToken.setRevokedAt(LocalDateTime.now(ZoneOffset.UTC));
            refreshTokenRepository.save(currentToken);
            throw new RefreshTokenRevokedException();
        }

        UserEntity user = userRepository.findById(claims.userId())
                .orElseThrow(UserNotFoundException::new);

        List<String> roles = authorizationQueryRepository.findRolesByUserId(user.getId());
        List<String> permissions = authorizationQueryRepository.findPermissionsByUserId(user.getId());

        JwtTokenBundle tokenBundle = jwtTokenService.issueTokenPair(
                user.getId(),
                user.getEmail(),
                roles,
                permissions
        );

        currentToken.setRevokedAt(LocalDateTime.now(ZoneOffset.UTC));
        refreshTokenRepository.save(currentToken);

        RefreshTokenEntity newRefreshToken = new RefreshTokenEntity();
        newRefreshToken.setTokenId(tokenBundle.refreshTokenId());
        newRefreshToken.setUserId(user.getId());
        newRefreshToken.setExpiresAt(LocalDateTime.ofInstant(tokenBundle.refreshExpiresAt(), ZoneOffset.UTC));
        newRefreshToken.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        refreshTokenRepository.save(newRefreshToken);

        return new CompleteAuthenticationResponse(
                tokenBundle.accessToken(),
                tokenBundle.refreshToken(),
                "Bearer",
                tokenBundle.accessExpiresInSeconds(),
                user.getId(),
                user.getEmail()
        );
    }
}
