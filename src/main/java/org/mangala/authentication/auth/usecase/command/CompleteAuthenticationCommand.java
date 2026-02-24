package org.mangala.authentication.auth.usecase.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompleteAuthenticationCommand(
        @NotBlank
        String credentialId,
        @NotNull
        String authenticatorData,
        @NotBlank
        String clientDataJSON,
        @NotBlank
        String signature,
        String userHandle,
        String userAgent,
        String ipAddress
) {
}
