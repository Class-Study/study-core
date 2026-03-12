INSERT INTO study.users (
    id,
    name,
    email,
    password_hash,
    role,
    status,
    created_at
) VALUES (
             gen_random_uuid(),
             'Jonathan Carvalho',
             'jonathanaparecido80@gmail.com',
             '$2y$10$owbJImj70beYAz6JKhD04.O7tCbMq7A7qvUizTHBaZCqMnTLrOK6W',  -- hash do bcrypt
             'ADMIN',
             'ACTIVE',
             NOW()
         );