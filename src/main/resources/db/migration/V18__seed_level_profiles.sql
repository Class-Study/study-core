WITH basic_profile AS (
    INSERT INTO level_profiles (id, name, code, icon, description, is_system, created_by)
    VALUES (
        gen_random_uuid(),
        'Basico (A1-A2)',
        'basic',
        '🟢',
        'Perfil padrao basico',
        TRUE,
        NULL
    )
    RETURNING id
)
INSERT INTO level_folders (id, level_profile_id, name, position, initial_files)
SELECT gen_random_uuid(), id, '1-TO DO', 1, 0 FROM basic_profile
UNION ALL
SELECT gen_random_uuid(), id, '2-IN PROGRESS', 2, 0 FROM basic_profile
UNION ALL
SELECT gen_random_uuid(), id, '3-VOCABULARY', 3, 0 FROM basic_profile
UNION ALL
SELECT gen_random_uuid(), id, '4-DONE', 4, 0 FROM basic_profile;

WITH intermediate_profile AS (
    INSERT INTO level_profiles (id, name, code, icon, description, is_system, created_by)
    VALUES (
        gen_random_uuid(),
        'Intermediario (B1-B2)',
        'intermediate',
        '🟡',
        'Perfil padrao intermediario',
        TRUE,
        NULL
    )
    RETURNING id
)
INSERT INTO level_folders (id, level_profile_id, name, position, initial_files)
SELECT gen_random_uuid(), id, '1-TO DO', 1, 4 FROM intermediate_profile
UNION ALL
SELECT gen_random_uuid(), id, '2-IN PROGRESS', 2, 0 FROM intermediate_profile
UNION ALL
SELECT gen_random_uuid(), id, '3-TO BE CORRECTED', 3, 0 FROM intermediate_profile
UNION ALL
SELECT gen_random_uuid(), id, '4-DONE', 4, 0 FROM intermediate_profile
UNION ALL
SELECT gen_random_uuid(), id, '5-VOCABULARY & PRES.', 5, 2 FROM intermediate_profile;

WITH advanced_profile AS (
    INSERT INTO level_profiles (id, name, code, icon, description, is_system, created_by)
    VALUES (
        gen_random_uuid(),
        'Avancado (C1-C2)',
        'advanced',
        '🔴',
        'Perfil padrao avancado',
        TRUE,
        NULL
    )
    RETURNING id
)
INSERT INTO level_folders (id, level_profile_id, name, position, initial_files)
SELECT gen_random_uuid(), id, '1-TO DO', 1, 5 FROM advanced_profile
UNION ALL
SELECT gen_random_uuid(), id, '2-IN PROGRESS', 2, 0 FROM advanced_profile
UNION ALL
SELECT gen_random_uuid(), id, '3-TO BE CORRECTED', 3, 0 FROM advanced_profile
UNION ALL
SELECT gen_random_uuid(), id, '4-DONE', 4, 0 FROM advanced_profile
UNION ALL
SELECT gen_random_uuid(), id, '5-EXAM PREPARATION', 5, 3 FROM advanced_profile;

