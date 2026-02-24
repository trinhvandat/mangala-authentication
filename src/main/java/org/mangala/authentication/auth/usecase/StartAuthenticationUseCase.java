package org.mangala.authentication.auth.usecase;

import jakarta.validation.Valid;
import org.mangala.authentication.auth.usecase.command.StartAuthenticationCommand;
import org.mangala.authentication.auth.usecase.model.StartAuthenticationResponse;

public interface StartAuthenticationUseCase {
    StartAuthenticationResponse execute(@Valid StartAuthenticationCommand command);
}
