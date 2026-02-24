package org.mangala.authentication.passkey.adapter.repository;

import org.mangala.authentication.passkey.domain.UserPasskeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPasskeyRepository extends JpaRepository<UserPasskeyEntity, UUID> {

    boolean existsByCredentialId(byte[] credentialId);

    Optional<UserPasskeyEntity> findByCredentialId(byte[] credentialId);

    List<UserPasskeyEntity> findByUserIdAndIsActiveTrue(UUID userId);

    Optional<UserPasskeyEntity> findByCredentialIdAndIsActiveTrue(byte[] credentialId);
}
