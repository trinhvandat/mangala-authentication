package org.mangala.authentication.passkey.adapter.repository;

import org.mangala.authentication.passkey.domain.PasskeyChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasskeyChallengeRepository extends JpaRepository<PasskeyChallengeEntity, UUID> {
}
