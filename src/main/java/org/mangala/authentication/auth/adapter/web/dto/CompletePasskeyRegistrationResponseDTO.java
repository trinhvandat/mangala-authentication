package org.mangala.authentication.auth.adapter.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CompletePasskeyRegistrationResponseDTO {

    private UUID userId;

    private String credentialId;  // Base64URL encoded

    private Boolean userVerified;

    private Boolean backupEligible;

    private Boolean backupState;

    private String deviceName;

    private LocalDateTime createdAt;
}
