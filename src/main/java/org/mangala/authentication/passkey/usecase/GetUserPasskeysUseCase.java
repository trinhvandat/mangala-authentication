package org.mangala.authentication.passkey.usecase;

import org.mangala.authentication.passkey.domain.UserPasskeyEntity;

import java.util.List;
import java.util.UUID;

public interface GetUserPasskeysUseCase {
    List<UserPasskeyEntity> execute(UUID userId);
}
