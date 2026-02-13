package org.mangala.authentication.auth.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record StartPasskeyAuthenticationRequestDTO(
        @JsonProperty("email")
        @NotBlank
        @Email
        String email
) {
}
