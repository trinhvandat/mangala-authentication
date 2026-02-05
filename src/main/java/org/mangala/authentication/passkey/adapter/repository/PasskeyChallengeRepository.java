package org.mangala.authentication.passkey.adapter.repository;

import org.mangala.authentication.passkey.domain.PasskeyChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasskeyChallengeRepository extends JpaRepository<PasskeyChallengeEntity, Long> {

    Optional<PasskeyChallengeEntity> findBySessionId(String sessionId);

    @Query("SELECT c FROM PasskeyChallengeEntity c WHERE c.sessionId = :sessionId " +
           "AND c.operationType = 'REGISTER' AND c.isUsed = false")
    Optional<PasskeyChallengeEntity> findValidRegistrationChallenge(
        @Param("sessionId") String sessionId
    );
}
