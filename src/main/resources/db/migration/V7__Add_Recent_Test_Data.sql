-- V7 Migration: Add recent test data for dashboard testing
-- This adds some recent tickets for the March 2025 event to show current data

-- Add recent ticket purchases for the upcoming workshop (Event 5)
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address, session_id
) VALUES 
-- Recent purchases (last 7 days)
('QR_RECENT_001', 'RECENT-001', 'recent1@test.com', 'Pavel Novák', false, DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, 5, 12, NULL, 'Czech Republic', 'Praha', '192.168.2.100', 'session_recent_1'),
('QR_RECENT_002', 'RECENT-002', 'recent2@test.com', 'Marie Svobodová', false, DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, 5, 13, NULL, 'Czech Republic', 'Brno', '192.168.2.101', 'session_recent_2'),
('QR_RECENT_003', 'RECENT-003', 'recent3@test.com', 'Jan Dvořák', false, DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, 5, 12, NULL, 'Slovakia', 'Bratislava', '192.168.2.102', 'session_recent_3'),
('QR_RECENT_004', 'RECENT-004', 'recent4@test.com', 'Petra Černá', false, DATE_SUB(NOW(), INTERVAL 4 DAY), NULL, 5, 12, NULL, 'Czech Republic', 'Ostrava', '192.168.2.103', 'session_recent_4'),
('QR_RECENT_005', 'RECENT-005', 'recent5@test.com', 'Tomáš Krejčí', false, DATE_SUB(NOW(), INTERVAL 5 DAY), NULL, 5, 13, NULL, 'Austria', 'Vienna', '192.168.2.104', 'session_recent_5'),
('QR_RECENT_006', 'RECENT-006', 'recent6@test.com', 'Anna Procházková', false, DATE_SUB(NOW(), INTERVAL 6 DAY), NULL, 5, 12, NULL, 'Czech Republic', 'Plzeň', '192.168.2.105', 'session_recent_6'),
('QR_RECENT_007', 'RECENT-007', 'recent7@test.com', 'Martin Veselý', false, DATE_SUB(NOW(), INTERVAL 7 DAY), NULL, 5, 12, NULL, 'Germany', 'Munich', '192.168.2.106', 'session_recent_7'),

-- Today's purchases
('QR_TODAY_001', 'TODAY-001', 'today1@test.com', 'Lucie Nováková', false, NOW(), NULL, 5, 12, NULL, 'Czech Republic', 'Praha', '192.168.2.200', 'session_today_1'),
('QR_TODAY_002', 'TODAY-002', 'today2@test.com', 'Filip Horák', false, NOW(), NULL, 5, 13, NULL, 'Czech Republic', 'Brno', '192.168.2.201', 'session_today_2'),

-- Last 30 days purchases  
('QR_MONTH_001', 'MONTH-001', 'month1@test.com', 'Eva Málková', false, DATE_SUB(NOW(), INTERVAL 10 DAY), NULL, 5, 12, NULL, 'Czech Republic', 'České Budějovice', '192.168.2.110', 'session_month_1'),
('QR_MONTH_002', 'MONTH-002', 'month2@test.com', 'David Novotný', false, DATE_SUB(NOW(), INTERVAL 15 DAY), NULL, 5, 13, NULL, 'Poland', 'Warsaw', '192.168.2.111', 'session_month_2'),
('QR_MONTH_003', 'MONTH-003', 'month3@test.com', 'Klára Růžičková', false, DATE_SUB(NOW(), INTERVAL 20 DAY), NULL, 5, 12, NULL, 'Czech Republic', 'Olomouc', '192.168.2.112', 'session_month_3'),
('QR_MONTH_004', 'MONTH-004', 'month4@test.com', 'Jakub Pokorný', false, DATE_SUB(NOW(), INTERVAL 25 DAY), NULL, 5, 12, NULL, 'Slovakia', 'Košice', '192.168.2.113', 'session_month_4'),
('QR_MONTH_005', 'MONTH-005', 'month5@test.com', 'Tereza Marková', false, DATE_SUB(NOW(), INTERVAL 28 DAY), NULL, 5, 13, NULL, 'Czech Republic', 'Hradec Králové', '192.168.2.114', 'session_month_5');

-- Add corresponding visitor sessions for recent purchases
INSERT INTO visitor_sessions (
    session_id, ip_address, user_agent, referer, traffic_source,
    country, country_code, region, city, event_id, user_id, timestamp,
    page_views, duration_seconds, converted, tickets_purchased, revenue_generated
)
SELECT 
    t.session_id,
    t.ip_address,
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36' as user_agent,
    CASE 
        WHEN RAND() < 0.3 THEN 'https://google.com'
        WHEN RAND() < 0.6 THEN 'https://facebook.com'
        ELSE 'direct'
    END as referer,
    CASE 
        WHEN RAND() < 0.4 THEN 'organic'
        WHEN RAND() < 0.7 THEN 'social'
        ELSE 'direct'
    END as traffic_source,
    t.country,
    CASE t.country
        WHEN 'Czech Republic' THEN 'CZ'
        WHEN 'Slovakia' THEN 'SK'
        WHEN 'Germany' THEN 'DE'
        WHEN 'Austria' THEN 'AT'
        WHEN 'Poland' THEN 'PL'
        ELSE 'CZ'
    END as country_code,
    'Region' as region,
    t.city,
    t.event_id,
    t.customer_id,
    t.purchase_date as timestamp,
    FLOOR(3 + RAND() * 10) as page_views,
    FLOOR(180 + RAND() * 900) as duration_seconds,
    true as converted,
    1 as tickets_purchased,
    tt.price as revenue_generated
FROM tickets t
JOIN ticket_types tt ON t.ticket_type_id = tt.id
WHERE t.session_id LIKE 'session_recent_%' 
   OR t.session_id LIKE 'session_today_%'
   OR t.session_id LIKE 'session_month_%';

-- Update ticket type quantities for Event 5
UPDATE ticket_types 
SET available_quantity = GREATEST(0, available_quantity - 14) 
WHERE event_id = 5 AND name = 'Standard';

UPDATE ticket_types 
SET available_quantity = GREATEST(0, available_quantity - 5) 
WHERE event_id = 5 AND name = 'VIP';

-- Add real-time stats for recent hours
INSERT INTO real_time_stats (
    event_id, timestamp, active_visitors, sales_last_minute, sales_last_hour,
    revenue_last_minute, revenue_last_hour, conversion_rate, bounce_rate,
    average_session_duration, pages_per_session
)
SELECT 
    5 as event_id,
    DATE_SUB(NOW(), INTERVAL hour_offset HOUR) as timestamp,
    FLOOR(10 + RAND() * 30) as active_visitors,
    CASE 
        WHEN hour_offset < 2 THEN FLOOR(RAND() * 3)
        ELSE 0
    END as sales_last_minute,
    CASE 
        WHEN hour_offset < 8 THEN FLOOR(1 + RAND() * 5)
        ELSE FLOOR(RAND() * 2)
    END as sales_last_hour,
    CASE 
        WHEN hour_offset < 2 THEN ROUND(RAND() * 1500, 2)
        ELSE 0
    END as revenue_last_minute,
    CASE 
        WHEN hour_offset < 8 THEN ROUND(500 + RAND() * 2500, 2)
        ELSE ROUND(RAND() * 1000, 2)
    END as revenue_last_hour,
    ROUND(20 + RAND() * 15, 1) as conversion_rate,
    ROUND(25 + RAND() * 20, 1) as bounce_rate,
    ROUND(300 + RAND() * 600, 1) as average_session_duration,
    ROUND(3 + RAND() * 4, 1) as pages_per_session
FROM (
    SELECT 0 as hour_offset UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL 
    SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL 
    SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12
) hours;