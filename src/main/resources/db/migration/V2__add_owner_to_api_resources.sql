-- Add owner (user_id) to api_resources
BEGIN;

ALTER TABLE api_resources
ADD COLUMN IF NOT EXISTS user_id BIGINT;

ALTER TABLE api_resources ADD CONSTRAINT IF NOT EXISTS fk_api_resources_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

COMMIT;G