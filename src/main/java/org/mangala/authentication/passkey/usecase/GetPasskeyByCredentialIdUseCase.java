package org.mangala.authentication.passkey.usecase;

import org.mangala.authentication.passkey.domain.UserPasskeyEntity;

public interface GetPasskeyByCredentialIdUseCase {
    UserPasskeyEntity execute(byte[] credentialId);
}
