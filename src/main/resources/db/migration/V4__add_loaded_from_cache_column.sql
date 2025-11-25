ALTER TABLE request_logs
ADD COLUMN loaded_from_cache BOOLEAN NOT NULL DEFAULT false;