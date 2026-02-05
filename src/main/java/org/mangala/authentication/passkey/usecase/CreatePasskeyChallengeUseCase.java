package org.mangala.authentication.passkey.usecase;

import jakarta.validation.Valid;
import org.mangala.authentication.passkey.domain.PasskeyChallengeEntity;
import org.mangala.authentication.passkey.usecase.command.CreatePasskeyChallengeCommand;

public interface CreatePasskeyChallengeUseCase {
    PasskeyChallengeEntity execute(@Valid CreatePasskeyChallengeCommand command);
}
