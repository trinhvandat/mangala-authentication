package org.mangala.authentication.passkey.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class AssertionVerificationFailedException extends BaseException {
    public AssertionVerificationFailedException() {
        super(ErrorConstant.ASSERTION_VERIFICATION_FAILED);
    }
}
