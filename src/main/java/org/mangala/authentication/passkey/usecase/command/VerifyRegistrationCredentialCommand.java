package org.mangala.authentication.passkey.usecase.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyRegistrationCredentialCommand {
    private byte[] challenge;
    private byte[] clientDataJSON;
    private byte[] attestationObject;
    private String ipAddress;
}
