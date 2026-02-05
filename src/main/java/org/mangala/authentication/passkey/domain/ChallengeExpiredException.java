package org.mangala.authentication.passkey.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class ChallengeExpiredException extends BaseException {
    public ChallengeExpiredException() {
        super(ErrorConstant.CHALLENGE_EXPIRED);
    }
}
