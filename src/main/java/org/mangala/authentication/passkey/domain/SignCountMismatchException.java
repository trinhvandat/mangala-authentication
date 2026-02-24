package org.mangala.authentication.passkey.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class SignCountMismatchException extends BaseException {
    public SignCountMismatchException() {
        super(ErrorConstant.SIGN_COUNT_MISMATCH);
    }
}
