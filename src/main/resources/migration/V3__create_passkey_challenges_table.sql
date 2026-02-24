CREATE TABLE IF NOT EXISTS passkey_challenges (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    challenge bytea NOT NULL,
    user_id uuid,
    operation_type varchar(50) NOT NULL,
    session_id varchar(64),
    ip_address inet,
    user_agent text,
    expires_at timestamp NOT NULL,
    user_verification_requirement varchar(50),
    is_used bool NOT NULL DEFAULT false,
    used_at timestamp,
    created_at timestamp NOT NULL
);

CREATE INDEX idx_passkey_challenges_user_id ON passkey_challenges(user_id);
CREATE INDEX idx_passkey_challenges_session_id ON passkey_challenges(session_id);
CREATE INDEX idx_passkey_challenges_expires_at ON passkey_challenges(expires_at) WHERE is_used = false;