package org.mangala.authentication.auth.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class InvalidTokenException extends BaseException {

    public InvalidTokenException() {
        super(ErrorConstant.TOKEN_INVALID);
    }
}
