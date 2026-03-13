CREATE TABLE level_folders
(
    id               UUID PRIMARY KEY,
    level_profile_id UUID         NOT NULL REFERENCES level_profiles (id) ON DELETE CASCADE,
    name             VARCHAR(100) NOT NULL,
    position         INT          NOT NULL DEFAULT 0,
    initial_files    INT          NOT NULL DEFAULT 0
);