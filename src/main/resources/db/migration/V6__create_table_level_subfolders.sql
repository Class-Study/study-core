-- Create level_subfolders table (real persisted subfolders inside each level_folder)
CREATE TABLE study.level_subfolders
(
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    level_folder_id UUID         NOT NULL REFERENCES study.level_folders (id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    position        INTEGER      NOT NULL DEFAULT 0,
    created_by      UUID         REFERENCES study.users (id),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_level_subfolders_folder ON study.level_subfolders (level_folder_id);
