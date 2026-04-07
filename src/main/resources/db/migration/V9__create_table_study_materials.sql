CREATE TABLE study.study_materials
(
    id                UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    level_folder_id   UUID         NOT NULL REFERENCES study.level_folders (id) ON DELETE CASCADE,
    subfolder_id      UUID         NOT NULL REFERENCES study.level_subfolders (id) ON DELETE CASCADE,
    subfolder_type    VARCHAR(20),
    title             VARCHAR(255) NOT NULL,
    material_type     VARCHAR(20)  NOT NULL,
    url               TEXT,
    converted_html    TEXT,
    original_filename VARCHAR(255),
    description       TEXT,
    created_by        UUID         NOT NULL REFERENCES study.users (id),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_study_material_subfolder_type CHECK (subfolder_type IN ('EXERCISES', 'STUDY_MATERIALS')),
    CONSTRAINT chk_study_material_type CHECK (material_type IN ('VIDEO', 'DOCUMENT', 'LINK'))
);

CREATE INDEX idx_study_materials_level_folder ON study.study_materials (level_folder_id);
CREATE INDEX idx_study_materials_subfolder_type ON study.study_materials (subfolder_type);
CREATE INDEX idx_study_materials_type ON study.study_materials (material_type);

