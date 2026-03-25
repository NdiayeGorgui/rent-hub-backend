INSERT INTO users (username, email, password, full_name, city, phone, roles, enabled, created_at)
SELECT 'admin', 'admin@renthub.com',
       '$2a$10$98HnKES3TS1nNvQWjRzwtOtlb3BvSpjGecgm8i1h/s7HEGsZSd1gm', -- password: Admin@123
       'RentHub Administrator', 'System', '0000000000', '{ROLE_ADMIN}', true, NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@renthub.com');