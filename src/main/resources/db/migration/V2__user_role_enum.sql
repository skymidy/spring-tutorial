-- Migration: convert user_roles.name from text to Postgres enum
-- Create the enum type
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role_enum') THEN
        CREATE TYPE user_role_enum AS ENUM ('DEFAULT', 'ADMIN');
    END IF;
END$$;

-- Add a new column of enum type, nullable for backfill
ALTER TABLE user_roles
  ADD COLUMN IF NOT EXISTS name_new user_role_enum;

-- Backfill values mapping old textual values to enum values
-- Map 'USER' -> 'DEFAULT', 'ADMIN' -> 'ADMIN', otherwise 'DEFAULT'
UPDATE user_roles SET name_new =
  CASE
    WHEN LOWER(name) IN ('user', 'default') THEN 'DEFAULT'::user_role_enum
    WHEN LOWER(name) = 'admin' THEN 'ADMIN'::user_role_enum
    ELSE 'DEFAULT'::user_role_enum
  END;

-- Set not null and default on new column
ALTER TABLE user_roles
  ALTER COLUMN name_new SET NOT NULL,
  ALTER COLUMN name_new SET DEFAULT 'DEFAULT';

-- Drop constraints that reference old column name if any (none expected here)
-- Replace old column: drop old and rename new (separate statements to avoid syntax issues)
ALTER TABLE user_roles DROP COLUMN IF EXISTS name;

ALTER TABLE user_roles RENAME COLUMN name_new TO name;

-- Ensure length constraint not needed for enum; keep uniqueness
CREATE UNIQUE INDEX IF NOT EXISTS ux_user_roles_name ON user_roles(name);

-- Clean up: nothing else
