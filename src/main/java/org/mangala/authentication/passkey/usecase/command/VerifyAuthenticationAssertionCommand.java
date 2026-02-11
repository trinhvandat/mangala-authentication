package org.mangala.authentication.passkey.usecase.command;

public record VerifyAuthenticationAssertionCommand(
        byte[] credentialId,
        byte[] authenticatorData,
        byte[] clientDataJSON,
        byte[] signature,
        byte[] userHandle,
        byte[] storedPublicKey,
        String expectedChallenge,
        String rpId,
        String origin,
        boolean userVerificationRequired
) {
}
