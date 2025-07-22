-- V5 Migration: Add Comprehensive Event Analytics Dummy Data
-- This migration adds realistic dummy analytics data for individual events
-- to ensure the EventAnalytics dashboard shows meaningful metrics instead of zeros

-- Add more tickets with realistic purchase patterns and better analytics data
-- First, let's get the current event IDs (assuming events 1-5 exist from DataLoader)

-- Add additional ticket purchases with specific patterns for each event
-- Event 1: Summer Rock Festival 2024 (High revenue event)
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address, session_id
)
SELECT 
    CONCAT('QR_FESTIVAL_', LPAD(seq + 1000, 6, '0')) as qr_code,
    CONCAT('FESTIVAL-', LPAD(seq + 1000, 6, '0')) as ticket_number,
    CONCAT('festival_customer_', seq, '@email.com') as customer_email,
    CASE seq % 10
        WHEN 0 THEN 'Jan Novák'
        WHEN 1 THEN 'Petra Svobodová'
        WHEN 2 THEN 'Tomáš Procházka'
        WHEN 3 THEN 'Anna Novotná'
        WHEN 4 THEN 'Martin Dvořák'
        WHEN 5 THEN 'Lucie Černá'
        WHEN 6 THEN 'Pavel Krejčí'
        WHEN 7 THEN 'Eva Horáková'
        WHEN 8 THEN 'David Pospíšil'
        ELSE 'Markéta Veselá'
    END as customer_name,
    CASE WHEN seq % 4 = 0 THEN false ELSE true END as used,
    DATE_SUB('2024-08-15', INTERVAL FLOOR(30 + RAND() * 60) DAY) as purchase_date,
    CASE WHEN seq % 4 = 0 THEN NULL ELSE '2024-08-15' END as used_date,
    1 as event_id, -- Rock Festival
    CASE 
        WHEN seq % 10 < 6 THEN 1  -- 60% Standard tickets (ID 1)
        WHEN seq % 10 < 9 THEN 2  -- 30% VIP tickets (ID 2) 
        ELSE 3                    -- 10% Backstage tickets (ID 3)
    END as ticket_type_id,
    CASE seq % 5 + 1 WHEN 6 THEN NULL ELSE seq % 5 + 1 END as customer_id,
    CASE seq % 8
        WHEN 0 THEN 'Czech Republic'
        WHEN 1 THEN 'Czech Republic'
        WHEN 2 THEN 'Czech Republic'
        WHEN 3 THEN 'Slovakia'
        WHEN 4 THEN 'Germany'
        WHEN 5 THEN 'Austria'
        WHEN 6 THEN 'Poland'
        ELSE 'Czech Republic'
    END as country,
    CASE seq % 12
        WHEN 0 THEN 'Praha'
        WHEN 1 THEN 'Brno'
        WHEN 2 THEN 'Ostrava'
        WHEN 3 THEN 'Plzeň'
        WHEN 4 THEN 'Bratislava'
        WHEN 5 THEN 'Košice'
        WHEN 6 THEN 'Berlin'
        WHEN 7 THEN 'Munich'
        WHEN 8 THEN 'Vienna'
        WHEN 9 THEN 'Salzburg'
        WHEN 10 THEN 'Warsaw'
        ELSE 'Krakow'
    END as city,
    CONCAT('203.0.', FLOOR(100 + RAND() * 50), '.', FLOOR(1 + RAND() * 254)) as ip_address,
    CONCAT('festival_session_', LPAD(seq, 4, '0')) as session_id
FROM (
    SELECT 1 as seq UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL
    SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL
    SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30 UNION ALL
    SELECT 31 UNION ALL SELECT 32 UNION ALL SELECT 33 UNION ALL SELECT 34 UNION ALL SELECT 35 UNION ALL
    SELECT 36 UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39 UNION ALL SELECT 40 UNION ALL
    SELECT 41 UNION ALL SELECT 42 UNION ALL SELECT 43 UNION ALL SELECT 44 UNION ALL SELECT 45 UNION ALL
    SELECT 46 UNION ALL SELECT 47 UNION ALL SELECT 48 UNION ALL SELECT 49 UNION ALL SELECT 50 UNION ALL
    SELECT 51 UNION ALL SELECT 52 UNION ALL SELECT 53 UNION ALL SELECT 54 UNION ALL SELECT 55 UNION ALL
    SELECT 56 UNION ALL SELECT 57 UNION ALL SELECT 58 UNION ALL SELECT 59 UNION ALL SELECT 60 UNION ALL
    SELECT 61 UNION ALL SELECT 62 UNION ALL SELECT 63 UNION ALL SELECT 64 UNION ALL SELECT 65 UNION ALL
    SELECT 66 UNION ALL SELECT 67 UNION ALL SELECT 68 UNION ALL SELECT 69 UNION ALL SELECT 70 UNION ALL
    SELECT 71 UNION ALL SELECT 72 UNION ALL SELECT 73 UNION ALL SELECT 74 UNION ALL SELECT 75 UNION ALL
    SELECT 76 UNION ALL SELECT 77 UNION ALL SELECT 78 UNION ALL SELECT 79 UNION ALL SELECT 80 UNION ALL
    SELECT 81 UNION ALL SELECT 82 UNION ALL SELECT 83 UNION ALL SELECT 84 UNION ALL SELECT 85 UNION ALL
    SELECT 86 UNION ALL SELECT 87 UNION ALL SELECT 88 UNION ALL SELECT 89 UNION ALL SELECT 90 UNION ALL
    SELECT 91 UNION ALL SELECT 92 UNION ALL SELECT 93 UNION ALL SELECT 94 UNION ALL SELECT 95 UNION ALL
    SELECT 96 UNION ALL SELECT 97 UNION ALL SELECT 98 UNION ALL SELECT 99 UNION ALL SELECT 100
) as numbers;

-- Event 2: Tech Conference Prague 2024 (Medium-high revenue event)
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address, session_id
)
SELECT 
    CONCAT('QR_TECH_', LPAD(seq + 2000, 6, '0')) as qr_code,
    CONCAT('TECH-', LPAD(seq + 2000, 6, '0')) as ticket_number,
    CONCAT('tech_attendee_', seq, '@company.com') as customer_email,
    CASE seq % 8
        WHEN 0 THEN 'Jiří Novák'
        WHEN 1 THEN 'Michaela Svobodová'
        WHEN 2 THEN 'Ondřej Dvořák'
        WHEN 3 THEN 'Klára Procházková'
        WHEN 4 THEN 'Jakub Černý'
        WHEN 5 THEN 'Tereza Novotná'
        WHEN 6 THEN 'Filip Krejčí'
        ELSE 'Veronika Horáková'
    END as customer_name,
    CASE WHEN seq % 5 = 0 THEN false ELSE true END as used,
    DATE_SUB('2024-09-20', INTERVAL FLOOR(30 + RAND() * 60) DAY) as purchase_date,
    CASE WHEN seq % 5 = 0 THEN NULL ELSE '2024-09-20' END as used_date,
    2 as event_id, -- Tech Conference
    CASE 
        WHEN seq % 10 < 7 THEN 4  -- 70% Standard tickets
        ELSE 5                    -- 30% VIP tickets
    END as ticket_type_id,
    CASE seq % 5 + 1 WHEN 6 THEN NULL ELSE seq % 5 + 1 END as customer_id,
    CASE seq % 6
        WHEN 0 THEN 'Czech Republic'
        WHEN 1 THEN 'Czech Republic'
        WHEN 2 THEN 'Slovakia'
        WHEN 3 THEN 'Germany'
        WHEN 4 THEN 'Austria'
        ELSE 'Poland'
    END as country,
    CASE seq % 10
        WHEN 0 THEN 'Praha'
        WHEN 1 THEN 'Brno'
        WHEN 2 THEN 'Ostrava'
        WHEN 3 THEN 'Bratislava'
        WHEN 4 THEN 'Berlin'
        WHEN 5 THEN 'Munich'
        WHEN 6 THEN 'Vienna'
        WHEN 7 THEN 'Warsaw'
        WHEN 8 THEN 'Plzeň'
        ELSE 'Olomouc'
    END as city,
    CONCAT('198.51.', FLOOR(100 + RAND() * 50), '.', FLOOR(1 + RAND() * 254)) as ip_address,
    CONCAT('tech_session_', LPAD(seq, 4, '0')) as session_id
FROM (
    SELECT 1 as seq UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL
    SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL
    SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30 UNION ALL
    SELECT 31 UNION ALL SELECT 32 UNION ALL SELECT 33 UNION ALL SELECT 34 UNION ALL SELECT 35 UNION ALL
    SELECT 36 UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39 UNION ALL SELECT 40 UNION ALL
    SELECT 41 UNION ALL SELECT 42 UNION ALL SELECT 43 UNION ALL SELECT 44 UNION ALL SELECT 45 UNION ALL
    SELECT 46 UNION ALL SELECT 47 UNION ALL SELECT 48 UNION ALL SELECT 49 UNION ALL SELECT 50 UNION ALL
    SELECT 51 UNION ALL SELECT 52 UNION ALL SELECT 53 UNION ALL SELECT 54 UNION ALL SELECT 55 UNION ALL
    SELECT 56 UNION ALL SELECT 57 UNION ALL SELECT 58 UNION ALL SELECT 59 UNION ALL SELECT 60 UNION ALL
    SELECT 61 UNION ALL SELECT 62 UNION ALL SELECT 63 UNION ALL SELECT 64 UNION ALL SELECT 65 UNION ALL
    SELECT 66 UNION ALL SELECT 67 UNION ALL SELECT 68 UNION ALL SELECT 69 UNION ALL SELECT 70 UNION ALL
    SELECT 71 UNION ALL SELECT 72 UNION ALL SELECT 73 UNION ALL SELECT 74 UNION ALL SELECT 75
) as numbers;

-- Event 3: Romeo a Julie (Theater - Medium revenue)
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address, session_id
)
SELECT 
    CONCAT('QR_THEATER_', LPAD(seq + 3000, 6, '0')) as qr_code,
    CONCAT('THEATER-', LPAD(seq + 3000, 6, '0')) as ticket_number,
    CONCAT('theater_lover_', seq, '@email.cz') as customer_email,
    CASE seq % 6
        WHEN 0 THEN 'Marie Nováková'
        WHEN 1 THEN 'Petr Svoboda'
        WHEN 2 THEN 'Alena Dvořáková'
        WHEN 3 THEN 'Tomáš Procházka'
        WHEN 4 THEN 'Zuzana Černá'
        ELSE 'Milan Novotný'
    END as customer_name,
    CASE WHEN seq % 6 = 0 THEN false ELSE true END as used,
    DATE_SUB('2024-10-05', INTERVAL FLOOR(30 + RAND() * 60) DAY) as purchase_date,
    CASE WHEN seq % 6 = 0 THEN NULL ELSE '2024-10-05' END as used_date,
    3 as event_id, -- Theater
    CASE 
        WHEN seq % 10 < 5 THEN 6  -- 50% Standard tickets
        WHEN seq % 10 < 8 THEN 7  -- 30% VIP tickets
        ELSE 8                    -- 20% Balcony tickets
    END as ticket_type_id,
    CASE seq % 5 + 1 WHEN 6 THEN NULL ELSE seq % 5 + 1 END as customer_id,
    CASE seq % 5
        WHEN 0 THEN 'Czech Republic'
        WHEN 1 THEN 'Czech Republic'
        WHEN 2 THEN 'Czech Republic'
        WHEN 3 THEN 'Slovakia'
        ELSE 'Austria'
    END as country,
    CASE seq % 8
        WHEN 0 THEN 'Praha'
        WHEN 1 THEN 'Praha'
        WHEN 2 THEN 'Brno'
        WHEN 3 THEN 'Ostrava'
        WHEN 4 THEN 'Bratislava'
        WHEN 5 THEN 'Vienna'
        WHEN 6 THEN 'Plzeň'
        ELSE 'České Budějovice'
    END as city,
    CONCAT('172.16.', FLOOR(1 + RAND() * 50), '.', FLOOR(1 + RAND() * 254)) as ip_address,
    CONCAT('theater_session_', LPAD(seq, 4, '0')) as session_id
FROM (
    SELECT 1 as seq UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL
    SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL
    SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30 UNION ALL
    SELECT 31 UNION ALL SELECT 32 UNION ALL SELECT 33 UNION ALL SELECT 34 UNION ALL SELECT 35 UNION ALL
    SELECT 36 UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39 UNION ALL SELECT 40 UNION ALL
    SELECT 41 UNION ALL SELECT 42 UNION ALL SELECT 43 UNION ALL SELECT 44 UNION ALL SELECT 45 UNION ALL
    SELECT 46 UNION ALL SELECT 47 UNION ALL SELECT 48 UNION ALL SELECT 49 UNION ALL SELECT 50
) as numbers;

-- Event 4: Fotbalový zápas Sparta vs Slavia (Sports - High revenue, high volume)
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address, session_id
)
SELECT 
    CONCAT('QR_FOOTBALL_', LPAD(seq + 4000, 6, '0')) as qr_code,
    CONCAT('FOOTBALL-', LPAD(seq + 4000, 6, '0')) as ticket_number,
    CONCAT('football_fan_', seq, '@email.cz') as customer_email,
    CASE seq % 12
        WHEN 0 THEN 'Pavel Novák'
        WHEN 1 THEN 'Jana Svobodová'
        WHEN 2 THEN 'Martin Dvořák'
        WHEN 3 THEN 'Kateřina Procházková'
        WHEN 4 THEN 'Lukáš Černý'
        WHEN 5 THEN 'Barbora Novotná'
        WHEN 6 THEN 'Michal Krejčí'
        WHEN 7 THEN 'Simona Horáková'
        WHEN 8 THEN 'Radek Pospíšil'
        WHEN 9 THEN 'Lenka Veselá'
        WHEN 10 THEN 'Adam Svoboda'
        ELSE 'Nikola Dvořáková'
    END as customer_name,
    CASE WHEN seq % 7 = 0 THEN false ELSE true END as used,
    DATE_SUB('2024-11-10', INTERVAL FLOOR(30 + RAND() * 60) DAY) as purchase_date,
    CASE WHEN seq % 7 = 0 THEN NULL ELSE '2024-11-10' END as used_date,
    4 as event_id, -- Football
    CASE 
        WHEN seq % 10 < 4 THEN 9   -- 40% Standard tickets
        WHEN seq % 10 < 7 THEN 10  -- 30% VIP tickets
        ELSE 11                    -- 30% Tribune tickets
    END as ticket_type_id,
    CASE seq % 5 + 1 WHEN 6 THEN NULL ELSE seq % 5 + 1 END as customer_id,
    CASE seq % 7
        WHEN 0 THEN 'Czech Republic'
        WHEN 1 THEN 'Czech Republic'
        WHEN 2 THEN 'Czech Republic'
        WHEN 3 THEN 'Czech Republic'
        WHEN 4 THEN 'Slovakia'
        WHEN 5 THEN 'Austria'
        ELSE 'Germany'
    END as country,
    CASE seq % 10
        WHEN 0 THEN 'Praha'
        WHEN 1 THEN 'Praha'
        WHEN 2 THEN 'Praha'
        WHEN 3 THEN 'Brno'
        WHEN 4 THEN 'Ostrava'
        WHEN 5 THEN 'Plzeň'
        WHEN 6 THEN 'Bratislava'
        WHEN 7 THEN 'Vienna'
        WHEN 8 THEN 'Berlin'
        ELSE 'Olomouc'
    END as city,
    CONCAT('10.0.', FLOOR(1 + RAND() * 50), '.', FLOOR(1 + RAND() * 254)) as ip_address,
    CONCAT('football_session_', LPAD(seq, 4, '0')) as session_id
FROM (
    SELECT 1 as seq UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL
    SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL
    SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30 UNION ALL
    SELECT 31 UNION ALL SELECT 32 UNION ALL SELECT 33 UNION ALL SELECT 34 UNION ALL SELECT 35 UNION ALL
    SELECT 36 UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39 UNION ALL SELECT 40 UNION ALL
    SELECT 41 UNION ALL SELECT 42 UNION ALL SELECT 43 UNION ALL SELECT 44 UNION ALL SELECT 45 UNION ALL
    SELECT 46 UNION ALL SELECT 47 UNION ALL SELECT 48 UNION ALL SELECT 49 UNION ALL SELECT 50 UNION ALL
    SELECT 51 UNION ALL SELECT 52 UNION ALL SELECT 53 UNION ALL SELECT 54 UNION ALL SELECT 55 UNION ALL
    SELECT 56 UNION ALL SELECT 57 UNION ALL SELECT 58 UNION ALL SELECT 59 UNION ALL SELECT 60 UNION ALL
    SELECT 61 UNION ALL SELECT 62 UNION ALL SELECT 63 UNION ALL SELECT 64 UNION ALL SELECT 65 UNION ALL
    SELECT 66 UNION ALL SELECT 67 UNION ALL SELECT 68 UNION ALL SELECT 69 UNION ALL SELECT 70 UNION ALL
    SELECT 71 UNION ALL SELECT 72 UNION ALL SELECT 73 UNION ALL SELECT 74 UNION ALL SELECT 75 UNION ALL
    SELECT 76 UNION ALL SELECT 77 UNION ALL SELECT 78 UNION ALL SELECT 79 UNION ALL SELECT 80 UNION ALL
    SELECT 81 UNION ALL SELECT 82 UNION ALL SELECT 83 UNION ALL SELECT 84 UNION ALL SELECT 85 UNION ALL
    SELECT 86 UNION ALL SELECT 87 UNION ALL SELECT 88 UNION ALL SELECT 89 UNION ALL SELECT 90 UNION ALL
    SELECT 91 UNION ALL SELECT 92 UNION ALL SELECT 93 UNION ALL SELECT 94 UNION ALL SELECT 95 UNION ALL
    SELECT 96 UNION ALL SELECT 97 UNION ALL SELECT 98 UNION ALL SELECT 99 UNION ALL SELECT 100 UNION ALL
    SELECT 101 UNION ALL SELECT 102 UNION ALL SELECT 103 UNION ALL SELECT 104 UNION ALL SELECT 105 UNION ALL
    SELECT 106 UNION ALL SELECT 107 UNION ALL SELECT 108 UNION ALL SELECT 109 UNION ALL SELECT 110 UNION ALL
    SELECT 111 UNION ALL SELECT 112 UNION ALL SELECT 113 UNION ALL SELECT 114 UNION ALL SELECT 115 UNION ALL
    SELECT 116 UNION ALL SELECT 117 UNION ALL SELECT 118 UNION ALL SELECT 119 UNION ALL SELECT 120
) as numbers;

-- Event 5: Kurz vaření italské kuchyně (Workshop - Lower revenue, boutique event)
INSERT INTO tickets (
    qr_code, ticket_number, customer_email, customer_name, used, purchase_date, used_date,
    event_id, ticket_type_id, customer_id, country, city, ip_address, session_id
)
SELECT 
    CONCAT('QR_COOKING_', LPAD(seq + 5000, 6, '0')) as qr_code,
    CONCAT('COOKING-', LPAD(seq + 5000, 6, '0')) as ticket_number,
    CONCAT('cooking_enthusiast_', seq, '@email.cz') as customer_email,
    CASE seq % 8
        WHEN 0 THEN 'Helena Nováková'
        WHEN 1 THEN 'Robert Svoboda'
        WHEN 2 THEN 'Ivana Dvořáková'
        WHEN 3 THEN 'Jan Procházka'
        WHEN 4 THEN 'Monika Černá'
        WHEN 5 THEN 'Štěpán Novotný'
        WHEN 6 THEN 'Gabriela Krejčová'
        ELSE 'Viktor Horák'
    END as customer_name,
    CASE WHEN seq % 8 = 0 THEN false ELSE true END as used,
    DATE_SUB('2025-03-15', INTERVAL FLOOR(30 + RAND() * 60) DAY) as purchase_date,
    CASE WHEN seq % 8 = 0 THEN NULL ELSE '2025-03-15' END as used_date,
    5 as event_id, -- Cooking Workshop
    CASE 
        WHEN seq % 10 < 8 THEN 12  -- 80% Standard tickets
        ELSE 13                    -- 20% VIP tickets
    END as ticket_type_id,
    CASE seq % 5 + 1 WHEN 6 THEN NULL ELSE seq % 5 + 1 END as customer_id,
    CASE seq % 4
        WHEN 0 THEN 'Czech Republic'
        WHEN 1 THEN 'Czech Republic'
        WHEN 2 THEN 'Slovakia'
        ELSE 'Austria'
    END as country,
    CASE seq % 6
        WHEN 0 THEN 'Brno'
        WHEN 1 THEN 'Brno'
        WHEN 2 THEN 'Praha'
        WHEN 3 THEN 'Ostrava'
        WHEN 4 THEN 'Bratislava'
        ELSE 'Vienna'
    END as city,
    CONCAT('192.168.', FLOOR(50 + RAND() * 50), '.', FLOOR(1 + RAND() * 254)) as ip_address,
    CONCAT('cooking_session_', LPAD(seq, 4, '0')) as session_id
FROM (
    SELECT 1 as seq UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL
    SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL
    SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30
) as numbers;

-- Update ticket type available quantities to reflect the new sales
-- Rock Festival (Event 1)
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 60) WHERE event_id = 1 AND name = 'Standard';
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 30) WHERE event_id = 1 AND name = 'VIP';
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 10) WHERE event_id = 1 AND name = 'Backstage';

-- Tech Conference (Event 2)
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 53) WHERE event_id = 2 AND name = 'Standard';
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 22) WHERE event_id = 2 AND name = 'VIP';

-- Theater (Event 3)
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 25) WHERE event_id = 3 AND name = 'Standard';
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 15) WHERE event_id = 3 AND name = 'VIP';
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 10) WHERE event_id = 3 AND name = 'Balkon';

-- Football (Event 4) 
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 48) WHERE event_id = 4 AND name = 'Standard';
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 36) WHERE event_id = 4 AND name = 'VIP';
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 36) WHERE event_id = 4 AND name = 'Tribuna';

-- Cooking Workshop (Event 5)
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 24) WHERE event_id = 5 AND name = 'Standard';
UPDATE ticket_types SET available_quantity = GREATEST(0, quantity - 6) WHERE event_id = 5 AND name = 'VIP';

-- Add corresponding visitor sessions for better analytics correlation
INSERT INTO visitor_sessions (
    session_id, ip_address, user_agent, referer, traffic_source,
    country, country_code, region, city, event_id, user_id, timestamp,
    page_views, duration_seconds, converted, tickets_purchased, revenue_generated
)
SELECT DISTINCT
    t.session_id,
    t.ip_address,
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36' as user_agent,
    CASE 
        WHEN RAND() < 0.3 THEN 'https://google.com'
        WHEN RAND() < 0.5 THEN 'https://facebook.com'
        WHEN RAND() < 0.7 THEN 'https://instagram.com'
        WHEN RAND() < 0.9 THEN 'direct'
        ELSE 'https://twitter.com'
    END as referer,
    CASE 
        WHEN RAND() < 0.4 THEN 'organic'
        WHEN RAND() < 0.6 THEN 'social'
        WHEN RAND() < 0.8 THEN 'direct'
        ELSE 'referral'
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
    FLOOR(2 + RAND() * 8) as page_views,
    FLOOR(120 + RAND() * 600) as duration_seconds,
    true as converted,
    COUNT(t.id) as tickets_purchased,
    SUM(tt.price) as revenue_generated
FROM tickets t
JOIN ticket_types tt ON t.ticket_type_id = tt.id
WHERE t.session_id IS NOT NULL 
  AND t.session_id LIKE '%session_%'
  AND NOT EXISTS (
      SELECT 1 FROM visitor_sessions vs WHERE vs.session_id = t.session_id
  )
GROUP BY t.session_id, t.ip_address, t.country, t.city, t.event_id, t.customer_id, t.purchase_date;

-- Add non-converting visitor sessions for realistic conversion rates
INSERT INTO visitor_sessions (
    session_id, ip_address, user_agent, referer, traffic_source,
    country, country_code, region, city, event_id, timestamp,
    page_views, duration_seconds, converted, tickets_purchased, revenue_generated
)
SELECT 
    CONCAT('non_convert_', event_seq, '_', visitor_seq) as session_id,
    CONCAT('203.0.', FLOOR(100 + RAND() * 50), '.', FLOOR(1 + RAND() * 254)) as ip_address,
    'Mozilla/5.0 (compatible; non-converting visitor)' as user_agent,
    CASE FLOOR(RAND() * 5)
        WHEN 0 THEN 'https://google.com'
        WHEN 1 THEN 'https://facebook.com'
        WHEN 2 THEN 'https://instagram.com'
        WHEN 3 THEN 'direct'
        ELSE 'https://twitter.com'
    END as referer,
    CASE FLOOR(RAND() * 4)
        WHEN 0 THEN 'organic'
        WHEN 1 THEN 'social'
        WHEN 2 THEN 'direct'
        ELSE 'referral'
    END as traffic_source,
    CASE FLOOR(RAND() * 5)
        WHEN 0 THEN 'Czech Republic'
        WHEN 1 THEN 'Slovakia'
        WHEN 2 THEN 'Germany'
        WHEN 3 THEN 'Austria'
        ELSE 'Poland'
    END as country,
    CASE FLOOR(RAND() * 5)
        WHEN 0 THEN 'CZ'
        WHEN 1 THEN 'SK'
        WHEN 2 THEN 'DE'
        WHEN 3 THEN 'AT'
        ELSE 'PL'
    END as country_code,
    'Region' as region,
    CASE FLOOR(RAND() * 8)
        WHEN 0 THEN 'Praha'
        WHEN 1 THEN 'Brno'
        WHEN 2 THEN 'Ostrava'
        WHEN 3 THEN 'Bratislava'
        WHEN 4 THEN 'Berlin'
        WHEN 5 THEN 'Vienna'
        WHEN 6 THEN 'Warsaw'
        ELSE 'Munich'
    END as city,
    event_seq as event_id,
    DATE_SUB(CASE event_seq 
        WHEN 1 THEN '2024-08-15'
        WHEN 2 THEN '2024-09-20'
        WHEN 3 THEN '2024-10-05'
        WHEN 4 THEN '2024-11-10'
        WHEN 5 THEN '2025-03-15'
    END, INTERVAL FLOOR(30 + RAND() * 60) DAY) as timestamp,
    FLOOR(1 + RAND() * 5) as page_views,
    FLOOR(30 + RAND() * 300) as duration_seconds,
    false as converted,
    0 as tickets_purchased,
    0.00 as revenue_generated
FROM (
    SELECT 1 as event_seq UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
) events
CROSS JOIN (
    SELECT 1 as visitor_seq UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL
    SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL
    SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL
    SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL
    SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL
    SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30 UNION ALL
    SELECT 31 UNION ALL SELECT 32 UNION ALL SELECT 33 UNION ALL SELECT 34 UNION ALL SELECT 35 UNION ALL
    SELECT 36 UNION ALL SELECT 37 UNION ALL SELECT 38 UNION ALL SELECT 39 UNION ALL SELECT 40 UNION ALL
    SELECT 41 UNION ALL SELECT 42 UNION ALL SELECT 43 UNION ALL SELECT 44 UNION ALL SELECT 45 UNION ALL
    SELECT 46 UNION ALL SELECT 47 UNION ALL SELECT 48 UNION ALL SELECT 49 UNION ALL SELECT 50
) visitors;

-- Add more real-time stats entries for recent hours for each event
INSERT INTO real_time_stats (
    event_id, timestamp, active_visitors, sales_last_minute, sales_last_hour,
    revenue_last_minute, revenue_last_hour, conversion_rate, bounce_rate,
    average_session_duration, pages_per_session
)
SELECT 
    event_id,
    DATE_SUB(CASE event_id 
        WHEN 1 THEN '2024-08-15'
        WHEN 2 THEN '2024-09-20'
        WHEN 3 THEN '2024-10-05'
        WHEN 4 THEN '2024-11-10'
        WHEN 5 THEN '2025-03-15'
    END, INTERVAL hour_offset HOUR) as timestamp,
    FLOOR(5 + RAND() * 50) as active_visitors,
    FLOOR(RAND() * 5) as sales_last_minute,
    FLOOR(RAND() * 25) as sales_last_hour,
    ROUND(RAND() * 2500, 2) as revenue_last_minute,
    ROUND(RAND() * 15000, 2) as revenue_last_hour,
    ROUND(15 + RAND() * 25, 1) as conversion_rate,
    ROUND(20 + RAND() * 30, 1) as bounce_rate,
    ROUND(200 + RAND() * 400, 1) as average_session_duration,
    ROUND(2 + RAND() * 6, 1) as pages_per_session
FROM (
    SELECT 1 as event_id UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
) events
CROSS JOIN (
    SELECT 1 as hour_offset UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL 
    SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL 
    SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL 
    SELECT 15 UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL 
    SELECT 20 UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24
) hours;

-- Final summary of what was added
SELECT 
    'Added tickets for all events' as summary,
    COUNT(*) as total_tickets_added
FROM tickets 
WHERE qr_code LIKE 'QR_%'

UNION ALL

SELECT 
    'Added visitor sessions' as summary,
    COUNT(*) as total_sessions_added
FROM visitor_sessions 
WHERE session_id LIKE '%session_%' OR session_id LIKE 'non_convert_%'

UNION ALL

SELECT 
    'Added real-time stats entries' as summary,
    COUNT(*) as total_stats_added
FROM real_time_stats 
WHERE timestamp >= DATE_SUB(NOW(), INTERVAL 24 HOUR);

-- Calculate final revenue per event for verification
SELECT 
    e.name as event_name,
    COUNT(t.id) as tickets_sold,
    SUM(tt.price) as total_revenue,
    AVG(tt.price) as average_ticket_price,
    COUNT(DISTINCT t.customer_id) as unique_customers,
    ROUND(COUNT(t.id) * 100.0 / SUM(tt.quantity), 2) as sales_percentage
FROM events e
LEFT JOIN tickets t ON e.id = t.event_id
LEFT JOIN ticket_types tt ON t.ticket_type_id = tt.id
GROUP BY e.id, e.name
ORDER BY total_revenue DESC;