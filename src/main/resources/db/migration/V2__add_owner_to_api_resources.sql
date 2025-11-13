-- V2__add_owner_to_api_resources.sql
-- Add owner (user_id) to api_resources
ALTER TABLE api_resources
ADD COLUMN user_id BIGINT;

-- Add foreign key constraint
ALTER TABLE api_resources 
ADD CONSTRAINT fk_api_resources_user 
FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;