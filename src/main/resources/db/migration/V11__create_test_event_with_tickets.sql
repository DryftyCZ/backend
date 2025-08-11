-- Create test event for QR scanner testing
INSERT INTO events (
    id, name, description, date, time, venue, address, 
    capacity, available_tickets, status, created_at, organizer_id
) VALUES (
    999, 
    'QR Scanner Test Event', 
    'Testovací událost pro ověření QR scanneru s 10 vstupenkami', 
    '2025-08-15', 
    '19:00:00', 
    'Test Venue', 
    'Testovací adresa 123, Praha', 
    10, 
    10, 
    'PUBLISHED', 
    NOW(),
    (SELECT id FROM users WHERE username = 'test')
);

-- Create ticket type for the test event
INSERT INTO ticket_types (
    id, name, description, price, quantity, available_quantity, 
    event_id, created_at
) VALUES (
    999, 
    'Standardní vstupenka', 
    'Běžná vstupenka na testovací event', 
    500.00, 
    10, 
    0,  -- všechny prodané
    999, 
    NOW()
);

-- Create 10 test tickets with QR codes (all purchased)
INSERT INTO tickets (
    id, qr_code, used, ticket_number, customer_email, customer_name, 
    purchase_date, event_id, ticket_type_id, country, city, ip_address
) VALUES 
(9991, 'QR-TEST-001-2025', false, 'TICKET-001', 'test1@example.com', 'Jan Novák', NOW() - INTERVAL '2' DAY, 999, 999, 'Czech Republic', 'Praha', '192.168.1.10'),
(9992, 'QR-TEST-002-2025', false, 'TICKET-002', 'test2@example.com', 'Anna Svobodová', NOW() - INTERVAL '2' DAY, 999, 999, 'Czech Republic', 'Brno', '192.168.1.11'),
(9993, 'QR-TEST-003-2025', false, 'TICKET-003', 'test3@example.com', 'Petr Dvořák', NOW() - INTERVAL '1' DAY, 999, 999, 'Czech Republic', 'Ostrava', '192.168.1.12'),
(9994, 'QR-TEST-004-2025', false, 'TICKET-004', 'test4@example.com', 'Marie Nováková', NOW() - INTERVAL '1' DAY, 999, 999, 'Czech Republic', 'Plzeň', '192.168.1.13'),
(9995, 'QR-TEST-005-2025', false, 'TICKET-005', 'test5@example.com', 'Tomáš Procházka', NOW() - INTERVAL '1' DAY, 999, 999, 'Slovakia', 'Bratislava', '192.168.1.14'),
(9996, 'QR-TEST-006-2025', false, 'TICKET-006', 'test6@example.com', 'Eva Horáková', NOW() - INTERVAL '12' HOUR, 999, 999, 'Slovakia', 'Košice', '192.168.1.15'),
(9997, 'QR-TEST-007-2025', false, 'TICKET-007', 'test7@example.com', 'Pavel Krejčí', NOW() - INTERVAL '12' HOUR, 999, 999, 'Czech Republic', 'České Budějovice', '192.168.1.16'),
(9998, 'QR-TEST-008-2025', false, 'TICKET-008', 'test8@example.com', 'Lucie Svoboda', NOW() - INTERVAL '6' HOUR, 999, 999, 'Czech Republic', 'Hradec Králové', '192.168.1.17'),
(9999, 'QR-TEST-009-2025', false, 'TICKET-009', 'test9@example.com', 'Martin Černý', NOW() - INTERVAL '3' HOUR, 999, 999, 'Czech Republic', 'Pardubice', '192.168.1.18'),
(10000, 'QR-TEST-010-2025', false, 'TICKET-010', 'test10@example.com', 'Tereza Krásná', NOW() - INTERVAL '1' HOUR, 999, 999, 'Czech Republic', 'Liberec', '192.168.1.19');

-- Update event available tickets count
UPDATE events SET available_tickets = 0 WHERE id = 999;

-- Update ticket type available quantity
UPDATE ticket_types SET available_quantity = 0 WHERE id = 999;