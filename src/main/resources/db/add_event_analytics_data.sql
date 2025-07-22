-- Direct SQL script to add Event Analytics Dummy Data
-- Run this directly in H2 Console: http://localhost:8080/h2-console
-- JDBC URL: jdbc:h2:file:./data/ticketing-db
-- Username: sa, Password: password

-- First, let's check current event and ticket type setup
SELECT 
    e.id as event_id,
    e.name as event_name,
    e.city,
    e.date,
    COUNT(tt.id) as ticket_types_count,
    COALESCE(SUM(tt.quantity), 0) as total_capacity,
    COUNT(t.id) as current_tickets_sold
FROM events e
LEFT JOIN ticket_types tt ON e.id = tt.event_id
LEFT JOIN tickets t ON e.id = t.event_id
GROUP BY e.id, e.name, e.city, e.date
ORDER BY e.id;

-- If you see events but low ticket counts, proceed with the following inserts:

-- Add tickets for Event 1: Summer Rock Festival 2024
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address
) VALUES 
-- Standard tickets (500 CZK each)
('QR_FESTIVAL_001', 'FESTIVAL-001', 'fan1@email.com', 'Jan Novák', true, DATEADD(DAY, -45, NOW()), DATEADD(DAY, -30, NOW()), 1, 1, 2, 'Czech Republic', 'Praha', '192.168.1.100'),
('QR_FESTIVAL_002', 'FESTIVAL-002', 'fan2@email.com', 'Petra Svobodová', true, DATEADD(DAY, -40, NOW()), DATEADD(DAY, -25, NOW()), 1, 1, 3, 'Czech Republic', 'Brno', '192.168.1.101'),
('QR_FESTIVAL_003', 'FESTIVAL-003', 'fan3@email.com', 'Tomáš Procházka', true, DATEADD(DAY, -35, NOW()), DATEADD(DAY, -20, NOW()), 1, 1, 4, 'Slovakia', 'Bratislava', '192.168.1.102'),
('QR_FESTIVAL_004', 'FESTIVAL-004', 'fan4@email.com', 'Anna Novotná', false, DATEADD(DAY, -30, NOW()), NULL, 1, 1, 5, 'Czech Republic', 'Ostrava', '192.168.1.103'),
('QR_FESTIVAL_005', 'FESTIVAL-005', 'fan5@email.com', 'Martin Dvořák', true, DATEADD(DAY, -25, NOW()), DATEADD(DAY, -15, NOW()), 1, 1, 2, 'Germany', 'Munich', '192.168.1.104'),
-- VIP tickets (1200 CZK each)
('QR_FESTIVAL_VIP_001', 'FESTIVAL-VIP-001', 'vip1@email.com', 'Lucie Černá', true, DATEADD(DAY, -42, NOW()), DATEADD(DAY, -28, NOW()), 1, 2, 3, 'Czech Republic', 'Praha', '192.168.2.100'),
('QR_FESTIVAL_VIP_002', 'FESTIVAL-VIP-002', 'vip2@email.com', 'Pavel Krejčí', true, DATEADD(DAY, -38, NOW()), DATEADD(DAY, -22, NOW()), 1, 2, 4, 'Austria', 'Vienna', '192.168.2.101'),
('QR_FESTIVAL_VIP_003', 'FESTIVAL-VIP-003', 'vip3@email.com', 'Eva Horáková', false, DATEADD(DAY, -33, NOW()), NULL, 1, 2, 5, 'Czech Republic', 'Brno', '192.168.2.102'),
-- Backstage tickets (2500 CZK each)
('QR_FESTIVAL_BACK_001', 'FESTIVAL-BACK-001', 'backstage1@email.com', 'David Pospíšil', true, DATEADD(DAY, -50, NOW()), DATEADD(DAY, -35, NOW()), 1, 3, 2, 'Czech Republic', 'Praha', '192.168.3.100'),
('QR_FESTIVAL_BACK_002', 'FESTIVAL-BACK-002', 'backstage2@email.com', 'Markéta Veselá', true, DATEADD(DAY, -47, NOW()), DATEADD(DAY, -32, NOW()), 1, 3, 3, 'Poland', 'Warsaw', '192.168.3.101');

-- Add tickets for Event 2: Tech Conference Prague 2024
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address
) VALUES 
-- Standard tickets (500 CZK each)
('QR_TECH_001', 'TECH-001', 'tech1@company.com', 'Jiří Novák', true, DATEADD(DAY, -50, NOW()), DATEADD(DAY, -35, NOW()), 2, 4, 2, 'Czech Republic', 'Praha', '198.51.100.100'),
('QR_TECH_002', 'TECH-002', 'tech2@company.com', 'Michaela Svobodová', true, DATEADD(DAY, -45, NOW()), DATEADD(DAY, -30, NOW()), 2, 4, 3, 'Slovakia', 'Bratislava', '198.51.100.101'),
('QR_TECH_003', 'TECH-003', 'tech3@company.com', 'Ondřej Dvořák', false, DATEADD(DAY, -40, NOW()), NULL, 2, 4, 4, 'Czech Republic', 'Brno', '198.51.100.102'),
('QR_TECH_004', 'TECH-004', 'tech4@company.com', 'Klára Procházková', true, DATEADD(DAY, -35, NOW()), DATEADD(DAY, -25, NOW()), 2, 4, 5, 'Germany', 'Berlin', '198.51.100.103'),
-- VIP tickets (1200 CZK each)
('QR_TECH_VIP_001', 'TECH-VIP-001', 'techvip1@company.com', 'Jakub Černý', true, DATEADD(DAY, -48, NOW()), DATEADD(DAY, -33, NOW()), 2, 5, 2, 'Czech Republic', 'Praha', '198.51.101.100'),
('QR_TECH_VIP_002', 'TECH-VIP-002', 'techvip2@company.com', 'Tereza Novotná', true, DATEADD(DAY, -43, NOW()), DATEADD(DAY, -28, NOW()), 2, 5, 3, 'Austria', 'Vienna', '198.51.101.101'),
('QR_TECH_VIP_003', 'TECH-VIP-003', 'techvip3@company.com', 'Filip Krejčí', false, DATEADD(DAY, -38, NOW()), NULL, 2, 5, 4, 'Czech Republic', 'Ostrava', '198.51.101.102');

-- Add tickets for Event 3: Romeo a Julie (Theater)
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address
) VALUES 
-- Standard tickets (500 CZK each)
('QR_THEATER_001', 'THEATER-001', 'theater1@email.cz', 'Marie Nováková', true, DATEADD(DAY, -45, NOW()), DATEADD(DAY, -25, NOW()), 3, 6, 2, 'Czech Republic', 'Praha', '172.16.1.100'),
('QR_THEATER_002', 'THEATER-002', 'theater2@email.cz', 'Petr Svoboda', true, DATEADD(DAY, -40, NOW()), DATEADD(DAY, -20, NOW()), 3, 6, 3, 'Czech Republic', 'Brno', '172.16.1.101'),
('QR_THEATER_003', 'THEATER-003', 'theater3@email.cz', 'Alena Dvořáková', false, DATEADD(DAY, -35, NOW()), NULL, 3, 6, 4, 'Slovakia', 'Bratislava', '172.16.1.102'),
-- VIP tickets (1200 CZK each)
('QR_THEATER_VIP_001', 'THEATER-VIP-001', 'theatervip1@email.cz', 'Tomáš Procházka', true, DATEADD(DAY, -42, NOW()), DATEADD(DAY, -22, NOW()), 3, 7, 5, 'Czech Republic', 'Praha', '172.16.2.100'),
('QR_THEATER_VIP_002', 'THEATER-VIP-002', 'theatervip2@email.cz', 'Zuzana Černá', true, DATEADD(DAY, -37, NOW()), DATEADD(DAY, -17, NOW()), 3, 7, 2, 'Austria', 'Vienna', '172.16.2.101'),
-- Balcony tickets (800 CZK each)
('QR_THEATER_BAL_001', 'THEATER-BAL-001', 'theaterbal1@email.cz', 'Milan Novotný', true, DATEADD(DAY, -44, NOW()), DATEADD(DAY, -24, NOW()), 3, 8, 3, 'Czech Republic', 'České Budějovice', '172.16.3.100'),
('QR_THEATER_BAL_002', 'THEATER-BAL-002', 'theaterbal2@email.cz', 'Jana Horáková', false, DATEADD(DAY, -39, NOW()), NULL, 3, 8, 4, 'Czech Republic', 'Plzeň', '172.16.3.101');

-- Add tickets for Event 4: Fotbalový zápas Sparta vs Slavia
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address
) VALUES 
-- Standard tickets (500 CZK each)
('QR_FOOTBALL_001', 'FOOTBALL-001', 'fan1@email.cz', 'Pavel Novák', true, DATEADD(DAY, -35, NOW()), DATEADD(DAY, -15, NOW()), 4, 9, 2, 'Czech Republic', 'Praha', '10.0.1.100'),
('QR_FOOTBALL_002', 'FOOTBALL-002', 'fan2@email.cz', 'Jana Svobodová', true, DATEADD(DAY, -30, NOW()), DATEADD(DAY, -10, NOW()), 4, 9, 3, 'Czech Republic', 'Praha', '10.0.1.101'),
('QR_FOOTBALL_003', 'FOOTBALL-003', 'fan3@email.cz', 'Martin Dvořák', true, DATEADD(DAY, -25, NOW()), DATEADD(DAY, -5, NOW()), 4, 9, 4, 'Czech Republic', 'Brno', '10.0.1.102'),
('QR_FOOTBALL_004', 'FOOTBALL-004', 'fan4@email.cz', 'Kateřina Procházková', false, DATEADD(DAY, -20, NOW()), NULL, 4, 9, 5, 'Slovakia', 'Bratislava', '10.0.1.103'),
-- VIP tickets (1200 CZK each)
('QR_FOOTBALL_VIP_001', 'FOOTBALL-VIP-001', 'vipfan1@email.cz', 'Lukáš Černý', true, DATEADD(DAY, -32, NOW()), DATEADD(DAY, -12, NOW()), 4, 10, 2, 'Czech Republic', 'Praha', '10.0.2.100'),
('QR_FOOTBALL_VIP_002', 'FOOTBALL-VIP-002', 'vipfan2@email.cz', 'Barbora Novotná', true, DATEADD(DAY, -27, NOW()), DATEADD(DAY, -7, NOW()), 4, 10, 3, 'Czech Republic', 'Praha', '10.0.2.101'),
-- Tribune tickets (700 CZK each)
('QR_FOOTBALL_TRIB_001', 'FOOTBALL-TRIB-001', 'tribfan1@email.cz', 'Michal Krejčí', true, DATEADD(DAY, -34, NOW()), DATEADD(DAY, -14, NOW()), 4, 11, 4, 'Czech Republic', 'Ostrava', '10.0.3.100'),
('QR_FOOTBALL_TRIB_002', 'FOOTBALL-TRIB-002', 'tribfan2@email.cz', 'Simona Horáková', true, DATEADD(DAY, -29, NOW()), DATEADD(DAY, -9, NOW()), 4, 11, 5, 'Austria', 'Vienna', '10.0.3.101'),
('QR_FOOTBALL_TRIB_003', 'FOOTBALL-TRIB-003', 'tribfan3@email.cz', 'Radek Pospíšil', false, DATEADD(DAY, -24, NOW()), NULL, 4, 11, 2, 'Czech Republic', 'Plzeň', '10.0.3.102');

-- Add tickets for Event 5: Kurz vaření italské kuchyně
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address
) VALUES 
-- Standard tickets (500 CZK each)
('QR_COOKING_001', 'COOKING-001', 'cook1@email.cz', 'Helena Nováková', true, DATEADD(DAY, -25, NOW()), DATEADD(DAY, -10, NOW()), 5, 12, 2, 'Czech Republic', 'Brno', '192.168.50.100'),
('QR_COOKING_002', 'COOKING-002', 'cook2@email.cz', 'Robert Svoboda', true, DATEADD(DAY, -20, NOW()), DATEADD(DAY, -8, NOW()), 5, 12, 3, 'Czech Republic', 'Praha', '192.168.50.101'),
('QR_COOKING_003', 'COOKING-003', 'cook3@email.cz', 'Ivana Dvořáková', false, DATEADD(DAY, -15, NOW()), NULL, 5, 12, 4, 'Slovakia', 'Bratislava', '192.168.50.102'),
-- VIP tickets (1200 CZK each)
('QR_COOKING_VIP_001', 'COOKING-VIP-001', 'cookvip1@email.cz', 'Jan Procházka', true, DATEADD(DAY, -22, NOW()), DATEADD(DAY, -9, NOW()), 5, 13, 5, 'Czech Republic', 'Brno', '192.168.51.100'),
('QR_COOKING_VIP_002', 'COOKING-VIP-002', 'cookvip2@email.cz', 'Monika Černá', true, DATEADD(DAY, -18, NOW()), DATEADD(DAY, -6, NOW()), 5, 13, 2, 'Austria', 'Vienna', '192.168.51.101');

-- Update ticket type available quantities
UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 5) WHERE event_id = 1 AND name = 'Standard';
UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 3) WHERE event_id = 1 AND name = 'VIP';
UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 2) WHERE event_id = 1 AND name = 'Backstage';

UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 4) WHERE event_id = 2 AND name = 'Standard';
UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 3) WHERE event_id = 2 AND name = 'VIP';

UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 3) WHERE event_id = 3 AND name = 'Standard';
UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 2) WHERE event_id = 3 AND name = 'VIP';
UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 2) WHERE event_id = 3 AND name = 'Balkon';

UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 4) WHERE event_id = 4 AND name = 'Standard';
UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 2) WHERE event_id = 4 AND name = 'VIP';
UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 3) WHERE event_id = 4 AND name = 'Tribuna';

UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 3) WHERE event_id = 5 AND name = 'Standard';
UPDATE ticket_types SET available_quantity = GREATEST(0, available_quantity - 2) WHERE event_id = 5 AND name = 'VIP';

-- Add corresponding visitor sessions
INSERT INTO visitor_sessions (
    session_id, ip_address, user_agent, referer, traffic_source,
    country, country_code, region, city, event_id, user_id, timestamp,
    page_views, duration_seconds, converted, tickets_purchased, revenue_generated
) VALUES 
-- Festival sessions
('festival_session_001', '192.168.1.100', 'Mozilla/5.0', 'https://google.com', 'organic', 'Czech Republic', 'CZ', 'Prague', 'Praha', 1, 2, DATEADD(DAY, -45, NOW()), 5, 420, true, 1, 500.00),
('festival_session_002', '192.168.1.101', 'Mozilla/5.0', 'https://facebook.com', 'social', 'Czech Republic', 'CZ', 'Brno', 'Brno', 1, 3, DATEADD(DAY, -40, NOW()), 3, 280, true, 1, 500.00),
('festival_vip_session_001', '192.168.2.100', 'Mozilla/5.0', 'https://instagram.com', 'social', 'Czech Republic', 'CZ', 'Prague', 'Praha', 1, 3, DATEADD(DAY, -42, NOW()), 8, 650, true, 1, 1200.00),
('festival_backstage_session_001', '192.168.3.100', 'Mozilla/5.0', 'direct', 'direct', 'Czech Republic', 'CZ', 'Prague', 'Praha', 1, 2, DATEADD(DAY, -50, NOW()), 12, 900, true, 1, 2500.00),
-- Tech sessions
('tech_session_001', '198.51.100.100', 'Mozilla/5.0', 'https://linkedin.com', 'social', 'Czech Republic', 'CZ', 'Prague', 'Praha', 2, 2, DATEADD(DAY, -50, NOW()), 6, 480, true, 1, 500.00),
('tech_session_002', '198.51.100.101', 'Mozilla/5.0', 'https://twitter.com', 'social', 'Slovakia', 'SK', 'Bratislava', 'Bratislava', 2, 3, DATEADD(DAY, -45, NOW()), 4, 350, true, 1, 500.00),
('tech_vip_session_001', '198.51.101.100', 'Mozilla/5.0', 'https://google.com', 'organic', 'Czech Republic', 'CZ', 'Prague', 'Praha', 2, 2, DATEADD(DAY, -48, NOW()), 10, 780, true, 1, 1200.00),
-- Theater sessions
('theater_session_001', '172.16.1.100', 'Mozilla/5.0', 'https://google.com', 'organic', 'Czech Republic', 'CZ', 'Prague', 'Praha', 3, 2, DATEADD(DAY, -45, NOW()), 4, 320, true, 1, 500.00),
('theater_vip_session_001', '172.16.2.100', 'Mozilla/5.0', 'direct', 'direct', 'Czech Republic', 'CZ', 'Prague', 'Praha', 3, 5, DATEADD(DAY, -42, NOW()), 7, 560, true, 1, 1200.00),
('theater_balcony_session_001', '172.16.3.100', 'Mozilla/5.0', 'https://facebook.com', 'social', 'Czech Republic', 'CZ', 'České Budějovice', 'České Budějovice', 3, 3, DATEADD(DAY, -44, NOW()), 5, 400, true, 1, 800.00),
-- Football sessions
('football_session_001', '10.0.1.100', 'Mozilla/5.0', 'https://google.com', 'organic', 'Czech Republic', 'CZ', 'Prague', 'Praha', 4, 2, DATEADD(DAY, -35, NOW()), 3, 210, true, 1, 500.00),
('football_vip_session_001', '10.0.2.100', 'Mozilla/5.0', 'direct', 'direct', 'Czech Republic', 'CZ', 'Prague', 'Praha', 4, 2, DATEADD(DAY, -32, NOW()), 6, 450, true, 1, 1200.00),
('football_tribune_session_001', '10.0.3.100', 'Mozilla/5.0', 'https://facebook.com', 'social', 'Czech Republic', 'CZ', 'Ostrava', 'Ostrava', 4, 4, DATEADD(DAY, -34, NOW()), 4, 280, true, 1, 700.00),
-- Cooking sessions
('cooking_session_001', '192.168.50.100', 'Mozilla/5.0', 'https://google.com', 'organic', 'Czech Republic', 'CZ', 'Brno', 'Brno', 5, 2, DATEADD(DAY, -25, NOW()), 5, 380, true, 1, 500.00),
('cooking_vip_session_001', '192.168.51.100', 'Mozilla/5.0', 'https://instagram.com', 'social', 'Czech Republic', 'CZ', 'Brno', 'Brno', 5, 5, DATEADD(DAY, -22, NOW()), 8, 620, true, 1, 1200.00);

-- Verify the data
SELECT 
    e.name as event_name,
    COUNT(t.id) as tickets_sold,
    SUM(CASE WHEN tt.name = 'Standard' THEN tt.price ELSE 0 END * 
        CASE WHEN tt.name = 'Standard' THEN COUNT(CASE WHEN t.ticket_type_id = tt.id THEN 1 END) ELSE 0 END) +
    SUM(CASE WHEN tt.name = 'VIP' THEN tt.price ELSE 0 END * 
        CASE WHEN tt.name = 'VIP' THEN COUNT(CASE WHEN t.ticket_type_id = tt.id THEN 1 END) ELSE 0 END) +
    SUM(CASE WHEN tt.name NOT IN ('Standard', 'VIP') THEN tt.price ELSE 0 END * 
        CASE WHEN tt.name NOT IN ('Standard', 'VIP') THEN COUNT(CASE WHEN t.ticket_type_id = tt.id THEN 1 END) ELSE 0 END) as estimated_revenue,
    COUNT(DISTINCT t.customer_id) as unique_customers,
    COUNT(DISTINCT vs.session_id) as visitor_sessions
FROM events e
LEFT JOIN tickets t ON e.id = t.event_id AND t.qr_code LIKE 'QR_%'
LEFT JOIN ticket_types tt ON e.id = tt.event_id
LEFT JOIN visitor_sessions vs ON e.id = vs.event_id AND vs.session_id LIKE '%session_%'
GROUP BY e.id, e.name
ORDER BY e.id;

-- Check actual revenue calculation per event
SELECT 
    e.id,
    e.name as event_name,
    COUNT(t.id) as tickets_sold,
    SUM(tt.price) as total_revenue,
    AVG(tt.price) as avg_ticket_price,
    COUNT(DISTINCT t.customer_id) as unique_customers
FROM events e
LEFT JOIN tickets t ON e.id = t.event_id AND t.qr_code LIKE 'QR_%'
LEFT JOIN ticket_types tt ON t.ticket_type_id = tt.id
GROUP BY e.id, e.name
ORDER BY total_revenue DESC;