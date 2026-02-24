package org.mangala.authentication.passkey.usecase.command;

public record UpdatePasskeySignCountCommand(
        byte[] credentialId,
        long newSignCount
) {
}
