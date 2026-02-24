package org.mangala.authentication.auth.usecase.command;

import jakarta.validation.constraints.Email;

public record StartAuthenticationCommand(
        @Email
        String email,
        String userAgent,
        String ipAddress
) {
}
