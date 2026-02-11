package org.mangala.authentication.passkey.usecase.model;

public record VerifyAuthenticationAssertionResponse(
        boolean userVerified,
        long signCount
) {
}
