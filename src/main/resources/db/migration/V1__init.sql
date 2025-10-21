-- Flyway migration: initial schema (created to match current JPA entities)
-- WARNING: This is an initial, authoritative schema for development. Applying
-- against a database with existing application data will fail or overwrite data.

-- user_roles
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    is_admin BOOLEAN DEFAULT FALSE
);

-- authentication_types
CREATE TABLE IF NOT EXISTS authentication_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- api_resources
CREATE TABLE IF NOT EXISTS api_resources (
    id BIGSERIAL PRIMARY KEY,
    authentication_type_id BIGINT REFERENCES authentication_types(id),
    name VARCHAR(255) NOT NULL,
    base_url VARCHAR(1024),
    is_enabled BOOLEAN DEFAULT TRUE,
    api_key VARCHAR(255)
);

-- users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    api_key VARCHAR(255) NOT NULL UNIQUE,
    role_id BIGINT REFERENCES user_roles(id),
    rate_limit BIGINT DEFAULT 0
);

-- request_logs
CREATE TABLE IF NOT EXISTS request_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    api_resource_id BIGINT REFERENCES api_resources(id),
    authentication_type_id BIGINT REFERENCES authentication_types(id),
    http_method VARCHAR(10),
    endpoint VARCHAR(2048),
    request_timestamp TIMESTAMP WITH TIME ZONE DEFAULT now(),
    response_status INTEGER,
    response_time_ms BIGINT,
    response_body_type VARCHAR(255)
);