package org.mangala.authentication.auth.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class RefreshTokenNotFoundException extends BaseException {

    public RefreshTokenNotFoundException() {
        super(ErrorConstant.REFRESH_TOKEN_NOT_FOUND);
    }
}
