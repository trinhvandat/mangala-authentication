CREATE TABLE IF NOT EXISTS user_passkeys (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL,
    public_key bytea NOT NULL,
    credential_id bytea NOT NULL UNIQUE,
    algorithm int NOT NULL,
    sign_count bigint NOT NULL,
    aaguid bytea,
    transports text[],
    backup_eligible boolean NOT NULL DEFAULT false,
    backup_state boolean NOT NULL DEFAULT false,
    device_name varchar(255),
    device_type varchar(255),
    attestation_format varchar(50),
    attestation_certificate bytea,
    user_verified boolean NOT NULL DEFAULT false,
    is_active boolean NOT NULL DEFAULT true,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at timestamp
);

CREATE INDEX idx_user_passkeys_user_id ON user_passkeys(user_id);
CREATE INDEX idx_user_passkeys_credential_id ON user_passkeys(credential_id);
