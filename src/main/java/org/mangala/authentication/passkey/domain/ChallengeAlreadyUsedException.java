package org.mangala.authentication.passkey.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class ChallengeAlreadyUsedException extends BaseException {
    public ChallengeAlreadyUsedException() {
        super(ErrorConstant.CHALLENGE_ALREADY_USED);
    }
}
