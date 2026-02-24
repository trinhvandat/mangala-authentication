package org.mangala.authentication.passkey.usecase;

import jakarta.validation.Valid;
import org.mangala.authentication.passkey.usecase.command.VerifyAuthenticationAssertionCommand;
import org.mangala.authentication.passkey.usecase.model.VerifyAuthenticationAssertionResponse;

public interface VerifyAuthenticationAssertionUseCase {
    VerifyAuthenticationAssertionResponse execute(@Valid VerifyAuthenticationAssertionCommand command);
}
