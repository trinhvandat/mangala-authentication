package org.mangala.authentication.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mangala.exception.ErrorDefinition;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorConstant implements ErrorDefinition {
    USER_EMAIL_ALREADY_EXISTED(HttpStatus.CONFLICT, "0000001", "User with email was already existed.");


    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
