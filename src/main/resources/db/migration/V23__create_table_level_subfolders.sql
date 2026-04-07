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

-- Add subfolder_id to level_folder_templates (nullable for backward compat)
ALTER TABLE study.level_folder_templates
    ADD COLUMN subfolder_id UUID REFERENCES study.level_subfolders (id) ON DELETE CASCADE;

CREATE INDEX idx_lft_subfolder ON study.level_folder_templates (subfolder_id);

-- Add subfolder_id to study_materials (replaces subfolder_type as canonical reference)
ALTER TABLE study.study_materials
    ADD COLUMN subfolder_id UUID REFERENCES study.level_subfolders (id) ON DELETE CASCADE;

-- Make subfolder_type nullable (was NOT NULL, now optional for backward compat)
ALTER TABLE study.study_materials
    ALTER COLUMN subfolder_type DROP NOT NULL;

CREATE INDEX idx_sm_subfolder ON study.study_materials (subfolder_id);

-- Data migration: create a default "Geral" subfolder for each level_folder
-- that already has templates or study_materials, then link them.

-- 1. Create default "Geral" subfolders for folders with existing content
INSERT INTO study.level_subfolders (id, level_folder_id, name, position, created_by, created_at, updated_at)
SELECT gen_random_uuid(), lf.id, 'Geral', 1,
       (SELECT lp.created_by FROM study.level_profiles lp WHERE lp.id = lf.level_profile_id LIMIT 1),
       NOW(), NOW()
FROM study.level_folders lf
WHERE EXISTS (
    SELECT 1 FROM study.level_folder_templates lft WHERE lft.level_folder_id = lf.id
)
OR EXISTS (
    SELECT 1 FROM study.study_materials sm WHERE sm.level_folder_id = lf.id
);

-- 2. Associate existing templates with the "Geral" subfolder
UPDATE study.level_folder_templates lft
SET subfolder_id = (
    SELECT ls.id FROM study.level_subfolders ls
    WHERE ls.level_folder_id = lft.level_folder_id
    ORDER BY ls.position ASC
    LIMIT 1
)
WHERE lft.subfolder_id IS NULL;

-- 3. Associate existing study_materials with the "Geral" subfolder
UPDATE study.study_materials sm
SET subfolder_id = (
    SELECT ls.id FROM study.level_subfolders ls
    WHERE ls.level_folder_id = sm.level_folder_id
    ORDER BY ls.position ASC
    LIMIT 1
)
WHERE sm.subfolder_id IS NULL;

