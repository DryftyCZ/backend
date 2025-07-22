-- V6 Migration: Add test tickets with specific dates for testing date filtering
-- This ensures we have tickets with purchase dates that match event dates

-- Add tickets with specific dates for Event 1 (August 2024)
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address, session_id
) VALUES 
-- Recent tickets for Event 1
('QR_AUG_001', 'AUG-001', 'aug1@test.com', 'August Customer 1', true, '2024-07-15 10:00:00', '2024-08-15 14:00:00', 1, 1, NULL, 'Czech Republic', 'Praha', '192.168.1.100', 'session_aug_1'),
('QR_AUG_002', 'AUG-002', 'aug2@test.com', 'August Customer 2', false, '2024-07-20 15:30:00', NULL, 1, 2, NULL, 'Czech Republic', 'Brno', '192.168.1.101', 'session_aug_2'),
('QR_AUG_003', 'AUG-003', 'aug3@test.com', 'August Customer 3', false, '2024-08-01 09:15:00', NULL, 1, 1, NULL, 'Czech Republic', 'Ostrava', '192.168.1.102', 'session_aug_3'),

-- Recent tickets for Event 2 (September 2024) 
('QR_SEP_001', 'SEP-001', 'sep1@test.com', 'September Customer 1', false, '2024-08-20 11:00:00', NULL, 2, 4, NULL, 'Czech Republic', 'Praha', '192.168.1.103', 'session_sep_1'),
('QR_SEP_002', 'SEP-002', 'sep2@test.com', 'September Customer 2', false, '2024-09-01 16:45:00', NULL, 2, 5, NULL, 'Slovakia', 'Bratislava', '192.168.1.104', 'session_sep_2');

-- Add tickets with earlier dates
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address, session_id
) VALUES 
-- Earlier tickets for Event 3 (October 2024)
('QR_OCT_001', 'OCT-001', 'oct1@test.com', 'October Customer 1', true, '2024-09-10 08:30:00', '2024-10-05 19:00:00', 3, 6, NULL, 'Czech Republic', 'Praha', '192.168.1.200', 'session_oct_1'),
('QR_OCT_002', 'OCT-002', 'oct2@test.com', 'October Customer 2', false, '2024-09-15 14:20:00', NULL, 3, 7, NULL, 'Czech Republic', 'Brno', '192.168.1.201', 'session_oct_2'),
('QR_OCT_003', 'OCT-003', 'oct3@test.com', 'October Customer 3', false, '2024-09-25 10:45:00', NULL, 3, 8, NULL, 'Czech Republic', 'Ostrava', '192.168.1.202', 'session_oct_3'),

-- Earlier tickets for Event 4 (November 2024)
('QR_NOV_001', 'NOV-001', 'nov1@test.com', 'November Customer 1', false, '2024-10-15 12:00:00', NULL, 4, 9, NULL, 'Czech Republic', 'Praha', '192.168.1.203', 'session_nov_1'),
('QR_NOV_002', 'NOV-002', 'nov2@test.com', 'November Customer 2', false, '2024-10-30 17:30:00', NULL, 4, 10, NULL, 'Slovakia', 'Bratislava', '192.168.1.204', 'session_nov_2');

-- Add tickets for Event 5 (March 2025 - plánovaný)
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address, session_id
) VALUES 
-- March tickets for Event 5
('QR_MAR_001', 'MAR-001', 'mar1@test.com', 'March Customer 1', false, '2025-01-15 09:00:00', NULL, 5, 12, NULL, 'Czech Republic', 'Brno', '192.168.1.300', 'session_mar_1'),
('QR_MAR_002', 'MAR-002', 'mar2@test.com', 'March Customer 2', false, '2025-02-01 13:30:00', NULL, 5, 13, NULL, 'Czech Republic', 'Brno', '192.168.1.301', 'session_mar_2'),
('QR_MAR_003', 'MAR-003', 'mar3@test.com', 'March Customer 3', false, '2025-02-20 15:15:00', NULL, 5, 12, NULL, 'Czech Republic', 'Praha', '192.168.1.302', 'session_mar_3');