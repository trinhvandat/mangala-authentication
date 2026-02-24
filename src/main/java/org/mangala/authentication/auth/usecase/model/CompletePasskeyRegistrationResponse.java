package org.mangala.authentication.auth.usecase.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CompletePasskeyRegistrationResponse {
    private UUID userId;
    private String credentialId;
    private Boolean userVerified;
    private Boolean backupEligible;
    private Boolean backupState;
    private String deviceName;
    private LocalDateTime createdAt;
}
