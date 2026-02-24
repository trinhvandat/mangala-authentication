package org.mangala.authentication.passkey.usecase;

import jakarta.validation.Valid;
import org.mangala.authentication.passkey.domain.UserPasskeyEntity;
import org.mangala.authentication.passkey.usecase.command.SavePasskeyCommand;

public interface SavePasskeyUseCase {
    UserPasskeyEntity execute(@Valid SavePasskeyCommand command);
}
