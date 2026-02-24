package org.mangala.authentication.auth.usecase.command;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CompletePasskeyRegistrationCommand {
    private String sessionId;
    private byte[] credentialId;
    private String credentialIdBase64Url;
    private byte[] clientDataJSON;
    private byte[] attestationObject;
    private List<String> transports;
    private String authenticatorAttachment;
    private String ipAddress;
    private String userAgent;
}
