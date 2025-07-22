-- Add test organizer user
INSERT INTO users (id, username, first_name, last_name, email, password, created_at) VALUES 
(99, 'organizer1', 'Jan', 'Novák', 'organizer1@example.com', '$2a$10$N.zmdr9k7uOeR6sYfmzn8OC2RJhlOaLDQCNhGJXJnrD/cHwczA9q2', NOW());

-- Add ORGANIZER role to the test user
INSERT INTO user_roles (user_id, role_name) VALUES (99, 'ORGANIZER');

-- Add commission record for the test organizer (25% commission)
INSERT INTO organizer_commissions (organizer_id, commission_percentage, is_active) VALUES 
(99, 25.00, TRUE);