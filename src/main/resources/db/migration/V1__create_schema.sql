-- Flyway migration: create initial schema for the application
-- PostgreSQL dialect
BEGIN;

-- Authentication types
CREATE TABLE
    IF NOT EXISTS authentication_types (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL UNIQUE
    );

-- API resources
CREATE TABLE
    IF NOT EXISTS api_resources (
        id SERIAL PRIMARY KEY,
        authentication_type_id INTEGER NOT NULL REFERENCES authentication_types (id),
        name VARCHAR(255) NOT NULL,
        base_url VARCHAR(255) NOT NULL,
        is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
        api_key VARCHAR(32) NOT NULL
    );

-- Users
CREATE TABLE
    IF NOT EXISTS users (
        id BIGSERIAL PRIMARY KEY,
        username VARCHAR(50) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        api_key VARCHAR(255) UNIQUE,
        rate_limit BIGINT DEFAULT 0,
        enabled BOOLEAN NOT NULL DEFAULT TRUE
    );

-- Authorities: composite PK (username, authority)
CREATE TABLE
    IF NOT EXISTS authorities (
        username VARCHAR(50) NOT NULL,
        authority VARCHAR(50) NOT NULL,
        PRIMARY KEY (username, authority),
        CONSTRAINT fk_authorities_user FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE
    );

-- Index specified on entity (unique over username,authority) - redundant with PK but kept to match annotation
CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username ON authorities (username, authority);

-- Request logs
CREATE TABLE
    IF NOT EXISTS request_logs (
        id SERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL REFERENCES users (id),
        api_resource_id INTEGER NOT NULL REFERENCES api_resources (id),
        authentication_type_id INTEGER NOT NULL REFERENCES authentication_types (id),
        http_method VARCHAR(10) NOT NULL,
        endpoint VARCHAR(2048) NOT NULL,
        request_timestamp TIMESTAMP
        WITH
            TIME ZONE NOT NULL DEFAULT now (),
            response_status INTEGER,
            response_time_ms BIGINT,
            response_body_type VARCHAR(50)
    );

COMMIT;