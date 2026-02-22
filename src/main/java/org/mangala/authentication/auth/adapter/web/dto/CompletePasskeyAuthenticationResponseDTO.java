package org.mangala.authentication.auth.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CompletePasskeyAuthenticationResponseDTO(
        @JsonProperty("accessToken")
        String accessToken,

        @JsonProperty("refreshToken")
        String refreshToken,

        @JsonProperty("tokenType")
        String tokenType,

        @JsonProperty("expiresIn")
        long expiresIn,

        @JsonProperty("refreshExpiresIn")
        long refreshExpiresIn,

        @JsonProperty("userId")
        UUID userId,

        @JsonProperty("email")
        String email
) {
}
