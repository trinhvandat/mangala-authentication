package org.mangala.authentication.user.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(ErrorConstant.USER_NOT_FOUND);
    }
}
