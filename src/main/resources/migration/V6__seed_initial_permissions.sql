-- =====================================================
-- Seed Initial Permissions, Roles, and API Permissions
-- =====================================================

-- Insert base permissions
INSERT INTO permissions (code, name, category, description) VALUES
-- Wallet permissions
('wallets:read', 'View Wallets', 'wallet', 'View wallet information'),
('wallets:create', 'Create Wallet', 'wallet', 'Create new wallet'),
('wallets:update', 'Update Wallet', 'wallet', 'Update wallet settings'),
('wallets:delete', 'Delete Wallet', 'wallet', 'Delete wallet'),

-- Address permissions
('addresses:read', 'View Addresses', 'wallet', 'View wallet addresses'),
('addresses:create', 'Create Address', 'wallet', 'Generate new address'),

-- Portfolio permissions
('portfolios:read', 'View Portfolio', 'portfolio', 'View portfolio information'),
('portfolios:export', 'Export Portfolio', 'portfolio', 'Export portfolio data'),

-- Holdings permissions
('holdings:read', 'View Holdings', 'portfolio', 'View holdings information'),

-- Transaction permissions
('transactions:read', 'View Transactions', 'transaction', 'View transaction history'),
('transactions:create', 'Create Transaction', 'transaction', 'Create new transaction'),
('transactions:export', 'Export Transactions', 'transaction', 'Export transaction data'),

-- User permissions
('users:read:self', 'View Own Profile', 'user', 'View own user profile'),
('users:update:self', 'Update Own Profile', 'user', 'Update own user profile'),

-- Admin permissions
('admin:users:read', 'View All Users', 'admin', 'View all users (admin)'),
('admin:users:write', 'Manage Users', 'admin', 'Create/update/delete users (admin)'),
('admin:roles:read', 'View Roles', 'admin', 'View all roles (admin)'),
('admin:roles:write', 'Manage Roles', 'admin', 'Create/update/delete roles (admin)'),
('admin:permissions:read', 'View Permissions', 'admin', 'View all permissions (admin)'),
('admin:permissions:write', 'Manage Permissions', 'admin', 'Manage permissions (admin)'),
('admin:policies:read', 'View Policies', 'admin', 'View API policies (admin)'),
('admin:policies:write', 'Manage Policies', 'admin', 'Manage API policies (admin)')
ON CONFLICT (code) DO NOTHING;

-- Insert roles
INSERT INTO roles (code, name, description) VALUES
('ROLE_USER', 'User', 'Standard user with basic access'),
('ROLE_PREMIUM', 'Premium User', 'Premium user with extended features'),
('ROLE_ADMIN', 'Administrator', 'System administrator with full access')
ON CONFLICT (code) DO NOTHING;

-- Assign permissions to ROLE_USER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ROLE_USER'
AND p.code IN (
    'wallets:read',
    'wallets:create',
    'addresses:read',
    'addresses:create',
    'portfolios:read',
    'holdings:read',
    'transactions:read',
    'transactions:create',
    'users:read:self',
    'users:update:self'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign permissions to ROLE_PREMIUM (all of ROLE_USER + extras)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ROLE_PREMIUM'
AND p.code IN (
    'wallets:read',
    'wallets:create',
    'wallets:update',
    'wallets:delete',
    'addresses:read',
    'addresses:create',
    'portfolios:read',
    'portfolios:export',
    'holdings:read',
    'transactions:read',
    'transactions:create',
    'transactions:export',
    'users:read:self',
    'users:update:self'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign all permissions to ROLE_ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ROLE_ADMIN'
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Insert API permissions (endpoint to permission mapping)
-- Wallet Service endpoints
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/wallets', p.id, 'wallet-service', 'List all wallets', 10
FROM permissions p WHERE p.code = 'wallets:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority, condition_expr)
SELECT 'GET', '/api/v1/wallets/{walletId}', p.id, 'wallet-service', 'Get wallet by ID', 10, NULL
FROM permissions p WHERE p.code = 'wallets:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'POST', '/api/v1/wallets', p.id, 'wallet-service', 'Create wallet', 10
FROM permissions p WHERE p.code = 'wallets:create'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'PUT', '/api/v1/wallets/{walletId}', p.id, 'wallet-service', 'Update wallet', 10
FROM permissions p WHERE p.code = 'wallets:update'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'DELETE', '/api/v1/wallets/{walletId}', p.id, 'wallet-service', 'Delete wallet', 10
FROM permissions p WHERE p.code = 'wallets:delete'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- Address endpoints
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/addresses', p.id, 'wallet-service', 'List addresses', 10
FROM permissions p WHERE p.code = 'addresses:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/addresses/{addressId}', p.id, 'wallet-service', 'Get address by ID', 10
FROM permissions p WHERE p.code = 'addresses:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'POST', '/api/v1/addresses', p.id, 'wallet-service', 'Create address', 10
FROM permissions p WHERE p.code = 'addresses:create'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- Portfolio endpoints
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/portfolios', p.id, 'portfolio-service', 'List portfolios', 10
FROM permissions p WHERE p.code = 'portfolios:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/portfolios/{portfolioId}', p.id, 'portfolio-service', 'Get portfolio by ID', 10
FROM permissions p WHERE p.code = 'portfolios:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/portfolios/{portfolioId}/export', p.id, 'portfolio-service', 'Export portfolio', 10
FROM permissions p WHERE p.code = 'portfolios:export'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- Holdings endpoints
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/holdings', p.id, 'portfolio-service', 'List holdings', 10
FROM permissions p WHERE p.code = 'holdings:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/holdings/{holdingId}', p.id, 'portfolio-service', 'Get holding by ID', 10
FROM permissions p WHERE p.code = 'holdings:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- Transaction endpoints
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/transactions', p.id, 'transaction-service', 'List transactions', 10
FROM permissions p WHERE p.code = 'transactions:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/transactions/{transactionId}', p.id, 'transaction-service', 'Get transaction by ID', 10
FROM permissions p WHERE p.code = 'transactions:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'POST', '/api/v1/transactions', p.id, 'transaction-service', 'Create transaction', 10
FROM permissions p WHERE p.code = 'transactions:create'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/transactions/export', p.id, 'transaction-service', 'Export transactions', 10
FROM permissions p WHERE p.code = 'transactions:export'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- Admin endpoints (wildcard)
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/admin/users', p.id, 'auth-service', 'List all users (admin)', 100
FROM permissions p WHERE p.code = 'admin:users:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/admin/users/{userId}', p.id, 'auth-service', 'Get user by ID (admin)', 100
FROM permissions p WHERE p.code = 'admin:users:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT '*', '/api/v1/admin/users/**', p.id, 'auth-service', 'Manage users (admin)', 50
FROM permissions p WHERE p.code = 'admin:users:write'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/admin/roles', p.id, 'auth-service', 'List roles (admin)', 100
FROM permissions p WHERE p.code = 'admin:roles:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT '*', '/api/v1/admin/roles/**', p.id, 'auth-service', 'Manage roles (admin)', 50
FROM permissions p WHERE p.code = 'admin:roles:write'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/admin/permissions', p.id, 'auth-service', 'List permissions (admin)', 100
FROM permissions p WHERE p.code = 'admin:permissions:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT '*', '/api/v1/admin/permissions/**', p.id, 'auth-service', 'Manage permissions (admin)', 50
FROM permissions p WHERE p.code = 'admin:permissions:write'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/admin/policies', p.id, 'auth-service', 'List policies (admin)', 100
FROM permissions p WHERE p.code = 'admin:policies:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT '*', '/api/v1/admin/policies/**', p.id, 'auth-service', 'Manage policies (admin)', 50
FROM permissions p WHERE p.code = 'admin:policies:write'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- Update policy version
UPDATE policy_version SET version = version + 1, updated_at = CURRENT_TIMESTAMP WHERE id = 1;
