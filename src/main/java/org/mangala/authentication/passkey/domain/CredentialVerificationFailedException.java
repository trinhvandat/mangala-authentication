package org.mangala.authentication.passkey.domain;

import org.mangala.authentication.shared.exception.ErrorConstant;
import org.mangala.exception.BaseException;

public class CredentialVerificationFailedException extends BaseException {
    public CredentialVerificationFailedException() {
        super(ErrorConstant.CREDENTIAL_VERIFICATION_FAILED);
    }
}
