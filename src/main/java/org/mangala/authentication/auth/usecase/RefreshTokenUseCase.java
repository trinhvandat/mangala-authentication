package org.mangala.authentication.auth.usecase;

import org.mangala.authentication.auth.usecase.command.RefreshTokenCommand;
import org.mangala.authentication.auth.usecase.model.CompleteAuthenticationResponse;

public interface RefreshTokenUseCase {

    CompleteAuthenticationResponse execute(RefreshTokenCommand command);
}
