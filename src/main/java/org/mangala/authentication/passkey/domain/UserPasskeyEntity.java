package org.mangala.authentication.passkey.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table(name = "user_passkeys")
@Entity
@Data
@NoArgsConstructor
public class UserPasskeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "public_key", nullable = false)
    private byte[] publicKey;

    @Column(name = "credential_id", nullable = false)
    private byte[] credentialId;

    /**
     * Value	Algorithm	Curve/Hash
     * -7	ES256	P-256 + SHA-256
     * -8	EdDSA	Ed25519
     * -35	ES384	P-384 + SHA-384
     * -36	ES512	P-521 + SHA-512
     * -37	PS256	RSA-PSS + SHA-256
     * -38	PS384	RSA-PSS + SHA-384
     * -39	PS512	RSA-PSS + SHA-512
     * -257	RS256	RSA-PKCS1 + SHA-256
     * -258	RS384	RSA-PKCS1 + SHA-384
     * -259	RS512	RSA-PKCS1 + SHA-512
     */
    @Column(name = "algorithm", nullable = false)
    private Integer algorithm;

    @Column(name = "sign_count", nullable = false)
    private Integer sighCount;

    @Column(name = "aaguid")
    private byte[] aaguid;

    @Column(name = "transports", columnDefinition = "text[]")
    private List<String> transports;

    @Column(name = "backup_eligible", nullable = false)
    private Boolean backupEligible = false;

    @Column(name = "backup_state", nullable = false)
    private Boolean backupState = false;

    @Column(name = "device_name", length = 255)
    private String deviceName;

    @Column(name = "device_type", length = 255)
    private String device_type;

    @Column(name = "attestation_format", length = 50)
    private String attestationFormat;

    @Column(name = "attestation_certificate")
    private byte[] attestationCertificate;

    @Column(name = "user_verified", nullable = false)
    private Boolean userVerified = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;
}
