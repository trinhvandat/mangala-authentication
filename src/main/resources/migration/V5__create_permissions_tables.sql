-- =====================================================
-- Permission System for ABAC Authorization
-- =====================================================

-- Permission definitions
CREATE TABLE IF NOT EXISTS permissions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(100) UNIQUE NOT NULL,
    name            VARCHAR(200) NOT NULL,
    description     VARCHAR(500),
    category        VARCHAR(50),
    is_active       BOOLEAN DEFAULT true,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255),
    last_updated_at TIMESTAMP,
    last_updated_by VARCHAR(255)
);

-- Role definitions
CREATE TABLE IF NOT EXISTS roles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code            VARCHAR(50) UNIQUE NOT NULL,
    name            VARCHAR(200) NOT NULL,
    description     VARCHAR(500),
    is_active       BOOLEAN DEFAULT true,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255),
    last_updated_at TIMESTAMP,
    last_updated_by VARCHAR(255)
);

-- Role to Permission mapping
CREATE TABLE IF NOT EXISTS role_permissions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id   UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255),

    UNIQUE(role_id, permission_id)
);

-- User to Role mapping
CREATE TABLE IF NOT EXISTS user_roles (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id         UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255),

    UNIQUE(user_id, role_id)
);

-- API Endpoint Permissions (ABAC Rules)
CREATE TABLE IF NOT EXISTS api_permissions (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Endpoint definition
    http_method     VARCHAR(10) NOT NULL,
    path_pattern    VARCHAR(500) NOT NULL,

    -- Required permission
    permission_id   UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,

    -- Optional ABAC conditions (SpEL expressions)
    condition_expr  VARCHAR(500),

    -- Metadata
    service_name    VARCHAR(100),
    description     VARCHAR(500),
    is_active       BOOLEAN DEFAULT true,
    priority        INTEGER DEFAULT 0,

    -- Auditing
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      VARCHAR(255),
    last_updated_at TIMESTAMP,
    last_updated_by VARCHAR(255),

    UNIQUE(http_method, path_pattern, permission_id)
);

-- Policy version for cache invalidation
CREATE TABLE IF NOT EXISTS policy_version (
    id              INTEGER PRIMARY KEY DEFAULT 1,
    version         BIGINT NOT NULL DEFAULT 1,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      VARCHAR(255),

    CONSTRAINT single_row CHECK (id = 1)
);

-- Initialize policy version
INSERT INTO policy_version (id, version) VALUES (1, 1) ON CONFLICT (id) DO NOTHING;

-- Indexes for fast lookup
CREATE INDEX idx_permissions_code ON permissions(code);
CREATE INDEX idx_permissions_active ON permissions(is_active) WHERE is_active = true;
CREATE INDEX idx_roles_code ON roles(code);
CREATE INDEX idx_roles_active ON roles(is_active) WHERE is_active = true;
CREATE INDEX idx_role_permissions_role ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission ON role_permissions(permission_id);
CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);
CREATE INDEX idx_api_permissions_path ON api_permissions(path_pattern, http_method);
CREATE INDEX idx_api_permissions_active ON api_permissions(is_active) WHERE is_active = true;
CREATE INDEX idx_api_permissions_service ON api_permissions(service_name);
