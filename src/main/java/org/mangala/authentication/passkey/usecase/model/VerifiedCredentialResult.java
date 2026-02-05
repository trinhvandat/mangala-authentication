package org.mangala.authentication.passkey.usecase.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifiedCredentialResult {
    private byte[] publicKey;
    private Integer algorithm;
    private byte[] credentialId;
    private byte[] aaguid;
    private Long signCount;
    private Boolean userVerified;
    private Boolean userPresent;
    private Boolean backupEligible;
    private Boolean backupState;
    private String attestationFormat;
    private byte[] attestationCertificate;
}
