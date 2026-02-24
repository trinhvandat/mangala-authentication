package org.mangala.authentication.auth.usecase;

import jakarta.validation.Valid;
import org.mangala.authentication.auth.usecase.command.CompletePasskeyRegistrationCommand;
import org.mangala.authentication.auth.usecase.model.CompletePasskeyRegistrationResponse;

public interface CompleteRegistrationUseCase {
    CompletePasskeyRegistrationResponse execute(@Valid CompletePasskeyRegistrationCommand command);
}
