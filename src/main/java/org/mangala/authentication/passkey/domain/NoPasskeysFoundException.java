package org.mangala.authentication.passkey.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class NoPasskeysFoundException extends BaseException {
    public NoPasskeysFoundException() {
        super(ErrorConstant.NO_PASSKEYS_FOUND);
    }
}
