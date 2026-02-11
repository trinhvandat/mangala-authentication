package org.mangala.authentication.auth.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PasskeyAuthenticationOptionsDTO(
        @JsonProperty("challenge")
        String challenge,

        @JsonProperty("rpId")
        String rpId,

        @JsonProperty("allowCredentials")
        List<AllowedCredentialDTO> allowCredentials,

        @JsonProperty("userVerification")
        String userVerification,

        @JsonProperty("timeout")
        long timeout
) {
    public record AllowedCredentialDTO(
            @JsonProperty("id")
            String id,

            @JsonProperty("type")
            String type
    ) {
    }
}
