package org.mangala.authentication.passkey.usecase;

import jakarta.validation.constraints.NotBlank;
import org.mangala.authentication.passkey.domain.PasskeyChallengeEntity;

public interface GetPasskeyChallengeUseCase {
    PasskeyChallengeEntity execute(@NotBlank String sessionId);
}
