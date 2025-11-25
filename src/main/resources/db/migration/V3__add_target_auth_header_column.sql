-- V3__add_target_auth_header_column
ALTER TABLE api_resources
ADD COLUMN target_auth_header VARCHAR(255) NULL;