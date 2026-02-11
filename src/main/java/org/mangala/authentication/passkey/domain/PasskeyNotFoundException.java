package org.mangala.authentication.passkey.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class PasskeyNotFoundException extends BaseException {
    public PasskeyNotFoundException() {
        super(ErrorConstant.PASSKEY_NOT_FOUND);
    }
}
