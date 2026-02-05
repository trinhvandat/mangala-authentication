package org.mangala.authentication.passkey.usecase.command;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SavePasskeyCommand {
    private UUID userId;
    private byte[] credentialId;
    private byte[] publicKey;
    private Integer algorithm;
    private Long signCount;
    private byte[] aaguid;
    private List<String> transports;
    private Boolean backupEligible;
    private Boolean backupState;
    private Boolean userVerified;
    private String attestationFormat;
    private byte[] attestationCertificate;
    private String deviceName;
}
