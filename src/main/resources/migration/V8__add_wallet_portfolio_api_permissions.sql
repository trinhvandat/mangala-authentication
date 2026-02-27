-- =====================================================
-- Add API Permissions for Wallet & Portfolio Services
-- Sprint 4: Integration & Testing
-- =====================================================
--
-- NOTE: This migration EXTENDS V6__seed_initial_permissions.sql
-- V6 already defines the following endpoint mappings:
--   - GET /api/v1/wallets → wallets:read
--   - GET /api/v1/wallets/{walletId} → wallets:read
--   - POST /api/v1/wallets → wallets:create
--   - PUT /api/v1/wallets/{walletId} → wallets:update
--   - DELETE /api/v1/wallets/{walletId} → wallets:delete
--   - GET /api/v1/portfolios → portfolios:read
--   - GET /api/v1/portfolios/{portfolioId} → portfolios:read
--   - GET /api/v1/portfolios/{portfolioId}/export → portfolios:export
--   - GET /api/v1/holdings → holdings:read
--   - GET /api/v1/holdings/{holdingId} → holdings:read
--
-- This migration adds NEW endpoints not covered in V6:
--   - Balance and sync endpoints for wallet service
--   - Portfolio CRUD (create, update, delete) endpoints
--   - Portfolio wallet management endpoints
--   - Portfolio summary, holdings, history, sync endpoints
-- =====================================================

-- Add new permissions for wallet and portfolio management
INSERT INTO permissions (code, name, category, description) VALUES
-- Wallet sync permission
('wallets:sync', 'Sync Wallet', 'wallet', 'Trigger wallet balance synchronization'),

-- Portfolio CRUD permissions
('portfolios:create', 'Create Portfolio', 'portfolio', 'Create new portfolio'),
('portfolios:update', 'Update Portfolio', 'portfolio', 'Update portfolio settings and wallets'),
('portfolios:delete', 'Delete Portfolio', 'portfolio', 'Delete portfolio')
ON CONFLICT (code) DO NOTHING;

-- Assign new wallet permissions to ROLE_USER
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ROLE_USER'
AND p.code IN (
    'wallets:sync',
    'portfolios:create',
    'portfolios:update'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign all new permissions to ROLE_PREMIUM
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ROLE_PREMIUM'
AND p.code IN (
    'wallets:sync',
    'portfolios:create',
    'portfolios:update',
    'portfolios:delete'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Assign all new permissions to ROLE_ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.code = 'ROLE_ADMIN'
AND p.code IN (
    'wallets:sync',
    'portfolios:create',
    'portfolios:update',
    'portfolios:delete'
)
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- =====================================================
-- Wallet Service API Permissions
-- =====================================================

-- GET /api/v1/wallets/{walletId}/balances
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/wallets/{walletId}/balances', p.id, 'wallet-service', 'Get wallet balances', 10
FROM permissions p WHERE p.code = 'wallets:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- POST /api/v1/wallets/{walletId}/sync
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'POST', '/api/v1/wallets/{walletId}/sync', p.id, 'wallet-service', 'Sync wallet balances', 10
FROM permissions p WHERE p.code = 'wallets:sync'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- =====================================================
-- Portfolio Service API Permissions
-- =====================================================

-- POST /api/v1/portfolios (create)
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'POST', '/api/v1/portfolios', p.id, 'portfolio-service', 'Create portfolio', 10
FROM permissions p WHERE p.code = 'portfolios:create'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- PUT /api/v1/portfolios/{portfolioId} (update)
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'PUT', '/api/v1/portfolios/{portfolioId}', p.id, 'portfolio-service', 'Update portfolio', 10
FROM permissions p WHERE p.code = 'portfolios:update'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- DELETE /api/v1/portfolios/{portfolioId} (delete)
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'DELETE', '/api/v1/portfolios/{portfolioId}', p.id, 'portfolio-service', 'Delete portfolio', 10
FROM permissions p WHERE p.code = 'portfolios:delete'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- POST /api/v1/portfolios/{portfolioId}/wallets (add wallets to portfolio)
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'POST', '/api/v1/portfolios/{portfolioId}/wallets', p.id, 'portfolio-service', 'Add wallets to portfolio', 10
FROM permissions p WHERE p.code = 'portfolios:update'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- DELETE /api/v1/portfolios/{portfolioId}/wallets/{walletId} (remove wallet from portfolio)
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'DELETE', '/api/v1/portfolios/{portfolioId}/wallets/{walletId}', p.id, 'portfolio-service', 'Remove wallet from portfolio', 10
FROM permissions p WHERE p.code = 'portfolios:update'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- GET /api/v1/portfolios/{portfolioId}/history
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/portfolios/{portfolioId}/history', p.id, 'portfolio-service', 'Get portfolio history', 10
FROM permissions p WHERE p.code = 'portfolios:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- GET /api/v1/portfolios/{portfolioId}/summary
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/portfolios/{portfolioId}/summary', p.id, 'portfolio-service', 'Get portfolio summary', 10
FROM permissions p WHERE p.code = 'portfolios:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- GET /api/v1/portfolios/{portfolioId}/holdings
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'GET', '/api/v1/portfolios/{portfolioId}/holdings', p.id, 'portfolio-service', 'Get portfolio holdings', 10
FROM permissions p WHERE p.code = 'holdings:read'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- POST /api/v1/portfolios/{portfolioId}/sync (trigger portfolio recalculation)
INSERT INTO api_permissions (http_method, path_pattern, permission_id, service_name, description, priority)
SELECT 'POST', '/api/v1/portfolios/{portfolioId}/sync', p.id, 'portfolio-service', 'Sync portfolio holdings', 10
FROM permissions p WHERE p.code = 'portfolios:update'
ON CONFLICT (http_method, path_pattern, permission_id) DO NOTHING;

-- Update policy version to trigger cache refresh
UPDATE policy_version SET version = version + 1, updated_at = CURRENT_TIMESTAMP WHERE id = 1;
