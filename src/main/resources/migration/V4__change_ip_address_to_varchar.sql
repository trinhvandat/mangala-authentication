-- Change ip_address column from inet to varchar(45)
-- This fixes the type mismatch between PostgreSQL inet type and JPA String type
-- varchar(45) accommodates both IPv4 (max 15 chars) and IPv6 (max 39 chars)

ALTER TABLE passkey_challenges
ALTER COLUMN ip_address TYPE varchar(45)
USING ip_address::varchar;
