package org.mangala.authentication.auth.adapter.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(@NotBlank String refreshToken) {
}
