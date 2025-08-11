-- Update test user from VISITOR to ORGANIZER role for QR scanner access

-- First, remove the existing VISITOR role for test user
DELETE FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'test') AND role_name = 'VISITOR';

-- Add ORGANIZER role to test user  
INSERT INTO user_roles (user_id, role_name) 
SELECT id, 'ORGANIZER' FROM users WHERE username = 'test' AND NOT EXISTS (
    SELECT 1 FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'test') AND role_name = 'ORGANIZER'
);

-- Also add a commission record for the test user as organizer (25% commission)
INSERT INTO organizer_commissions (organizer_id, commission_percentage, is_active) 
SELECT id, 25.00, TRUE FROM users WHERE username = 'test' AND NOT EXISTS (
    SELECT 1 FROM organizer_commissions WHERE organizer_id = (SELECT id FROM users WHERE username = 'test')
);