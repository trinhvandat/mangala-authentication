package org.mangala.authentication.passkey.usecase;

import jakarta.validation.Valid;
import org.mangala.authentication.passkey.usecase.command.VerifyRegistrationCredentialCommand;
import org.mangala.authentication.passkey.usecase.model.VerifiedCredentialResult;

public interface VerifyRegistrationCredentialUseCase {
    VerifiedCredentialResult execute(@Valid VerifyRegistrationCredentialCommand command);
}
