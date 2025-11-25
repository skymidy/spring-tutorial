-- V4__add_loaded_from_cache_column
ALTER TABLE request_logs
ADD COLUMN loaded_from_cache BOOLEAN NOT NULL DEFAULT false;