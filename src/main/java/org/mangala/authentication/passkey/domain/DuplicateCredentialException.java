package org.mangala.authentication.passkey.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class DuplicateCredentialException extends BaseException {
    public DuplicateCredentialException() {
        super(ErrorConstant.DUPLICATE_CREDENTIAL);
    }
}
