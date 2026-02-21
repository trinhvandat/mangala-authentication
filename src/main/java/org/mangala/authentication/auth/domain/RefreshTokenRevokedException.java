package org.mangala.authentication.auth.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class RefreshTokenRevokedException extends BaseException {

    public RefreshTokenRevokedException() {
        super(ErrorConstant.REFRESH_TOKEN_REVOKED);
    }
}
