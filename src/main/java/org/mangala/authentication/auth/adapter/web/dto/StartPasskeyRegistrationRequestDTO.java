package org.mangala.authentication.auth.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;

public record StartPasskeyRegistrationRequestDTO(
        @JsonProperty("email")
        @Email
        String email,

        @JsonProperty("displayName")
        String displayName
) {
}
