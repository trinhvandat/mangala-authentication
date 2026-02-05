package org.mangala.authentication.auth.usecase;

import org.mangala.authentication.auth.usecase.model.StartRegisterPasskeyResponse;

import java.util.Objects;

public interface StartRegistrationUseCase {
    StartRegisterPasskeyResponse execute(String email, String ipAddress, String userAgent);
}
