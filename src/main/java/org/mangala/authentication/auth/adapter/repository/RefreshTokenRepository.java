package org.mangala.authentication.auth.adapter.repository;

import org.mangala.authentication.auth.domain.RefreshTokenEntity;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenId(String tokenId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshTokenEntity> findWithLockByTokenId(String tokenId);
}
