package org.mangala.authentication.auth.usecase.command;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenCommand(@NotBlank String refreshToken) {
}
