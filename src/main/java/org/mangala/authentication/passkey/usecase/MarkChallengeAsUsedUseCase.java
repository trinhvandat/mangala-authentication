package org.mangala.authentication.passkey.usecase;

import jakarta.validation.constraints.NotNull;

public interface MarkChallengeAsUsedUseCase {
    void execute(@NotNull Long challengeId);
}
