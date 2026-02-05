package org.mangala.authentication.passkey.usecase;

import jakarta.validation.constraints.NotNull;

public interface CheckDuplicateCredentialUseCase {
    void execute(@NotNull byte[] credentialId);
}
