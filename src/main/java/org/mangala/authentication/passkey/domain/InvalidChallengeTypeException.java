package org.mangala.authentication.passkey.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class InvalidChallengeTypeException extends BaseException {
    public InvalidChallengeTypeException() {
        super(ErrorConstant.INVALID_CHALLENGE_TYPE);
    }
}
