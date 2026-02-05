package org.mangala.authentication.passkey.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class ChallengeNotFoundException extends BaseException {
    public ChallengeNotFoundException() {
        super(ErrorConstant.CHALLENGE_NOT_FOUND);
    }
}
