ALTER TABLE api_resources
ADD CONSTRAINT uk_api_resource_name UNIQUE (name);