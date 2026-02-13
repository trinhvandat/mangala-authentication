package org.mangala.authentication.auth.usecase;

import jakarta.validation.Valid;
import org.mangala.authentication.auth.usecase.command.CompleteAuthenticationCommand;
import org.mangala.authentication.auth.usecase.model.CompleteAuthenticationResponse;

public interface CompleteAuthenticationUseCase {
    CompleteAuthenticationResponse execute(@Valid CompleteAuthenticationCommand command);
}
