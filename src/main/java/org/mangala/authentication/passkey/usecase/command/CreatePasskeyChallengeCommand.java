package org.mangala.authentication.passkey.usecase.command;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.mangala.authentication.passkey.domain.PasskeyChallengeOperationType;

import java.time.LocalDateTime;

@Data
@Builder
public class CreatePasskeyChallengeCommand {
    private byte[] challenge;
    @Builder.Default
    private String userId = null;
    @NotNull(message = "OPERATION_TYPE_REQUIRED")
    private PasskeyChallengeOperationType operationType;
    @NotNull(message = "SESSION_ID_REQUIRED")
    private String sessionId;
    @NotNull(message = "IP_ADDRESS_REQUIRED")
    private String ipAddress;
    @NotNull(message = "USER_AGENT_REQUIRED")
    private String userAgent;
    @NotNull(message = "EXPIRES_AT_REQUIRED")
    private LocalDateTime expiresAt;
    @Builder.Default
    private String userVerificationRequirement = null;
}
