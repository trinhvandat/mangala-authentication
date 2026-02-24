package org.mangala.authentication.auth.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CompletePasskeyAuthenticationRequestDTO(
        @JsonProperty("credentialId")
        @NotBlank
        String credentialId,

        @JsonProperty("authenticatorData")
        @NotBlank
        String authenticatorData,

        @JsonProperty("clientDataJSON")
        @NotBlank
        String clientDataJSON,

        @JsonProperty("signature")
        @NotBlank
        String signature,

        @JsonProperty("userHandle")
        String userHandle
) {
}
