package org.mangala.authentication.user.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class UserWithEmailExistedException extends BaseException {
    public UserWithEmailExistedException() {
        super(ErrorConstant.USER_EMAIL_ALREADY_EXISTED);
    }
}
