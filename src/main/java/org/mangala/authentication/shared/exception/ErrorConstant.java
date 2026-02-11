package org.mangala.authentication.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mangala.exception.ErrorDefinition;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorConstant implements ErrorDefinition {
    // User errors
    USER_EMAIL_ALREADY_EXISTED(HttpStatus.CONFLICT, "0000001", "User with email was already existed."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "0000009", "User not found."),

    // Challenge errors
    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND, "0000002", "Challenge not found for the given session."),
    CHALLENGE_EXPIRED(HttpStatus.BAD_REQUEST, "0000003", "Challenge has expired."),
    CHALLENGE_ALREADY_USED(HttpStatus.BAD_REQUEST, "0000004", "Challenge has already been used."),

    // Credential errors
    CREDENTIAL_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "0000005", "Credential verification failed."),
    DUPLICATE_CREDENTIAL(HttpStatus.CONFLICT, "0000006", "Credential ID already exists."),
    INVALID_ORIGIN(HttpStatus.BAD_REQUEST, "0000007", "Origin validation failed."),
    ATTESTATION_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "0000008", "Attestation verification failed."),

    // Passkey errors
    PASSKEY_NOT_FOUND(HttpStatus.NOT_FOUND, "0000010", "Passkey not found."),
    NO_PASSKEYS_FOUND(HttpStatus.BAD_REQUEST, "0000011", "No passkeys registered for this user."),
    ASSERTION_VERIFICATION_FAILED(HttpStatus.UNAUTHORIZED, "0000012", "Authentication assertion verification failed."),
    SIGN_COUNT_MISMATCH(HttpStatus.UNAUTHORIZED, "0000013", "Sign count mismatch detected. Possible cloned authenticator."),
    INVALID_CHALLENGE_TYPE(HttpStatus.BAD_REQUEST, "0000014", "Invalid challenge type for operation.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
