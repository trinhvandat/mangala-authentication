package org.mangala.authentication.user.usecase;

import jakarta.validation.constraints.NotNull;

public interface CheckUserExistedUseCase {
    boolean execute(@NotNull String email);
}
