package org.mangala.authentication.passkey.usecase;

import org.mangala.authentication.passkey.usecase.command.UpdatePasskeySignCountCommand;

public interface UpdatePasskeySignCountUseCase {
    void execute(UpdatePasskeySignCountCommand command);
}
