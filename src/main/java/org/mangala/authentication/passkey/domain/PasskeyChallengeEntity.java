package org.mangala.authentication.passkey.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "passkey_challenges")
@Entity
@Data
@NoArgsConstructor
public class PasskeyChallengeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "challenge", nullable = false, columnDefinition = "bytea")
    private byte[] challenge;

    @Column(name = "user_id", nullable = true)
    private UUID userId;

    @Column(name = "operation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PasskeyChallengeOperationType operationType;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "user_verification_requirement")
    private String userVerificationRequirement;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
