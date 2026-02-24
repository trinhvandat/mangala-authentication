CREATE TABLE IF NOT EXISTS users (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email varchar(255) UNIQUE,
    email_verified boolean DEFAULT false,
    created_by varchar(255),
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated_by varchar(255),
    last_updated_at timestamp
);

CREATE INDEX idx_users_email ON users(email) WHERE email IS NOT NULL;