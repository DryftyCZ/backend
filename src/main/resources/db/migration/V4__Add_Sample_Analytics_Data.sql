-- Sample data for analytics to make dashboard show realistic numbers
-- This migration adds comprehensive sample data for visitor sessions and real-time stats

-- Insert sample visitor sessions with realistic data spanning last 30 days
INSERT INTO visitor_sessions (
    session_id, ip_address, user_agent, referer, traffic_source, 
    country, country_code, region, city, latitude, longitude, timezone, isp,
    event_id, user_id, timestamp, page_views, duration_seconds, converted, 
    tickets_purchased, revenue_generated
) VALUES 
-- Week 1 data (Recent)
('session_001', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://google.com', 'organic', 'Czech Republic', 'CZ', 'Prague', 'Praha', 50.0755, 14.4378, 'Europe/Prague', 'O2 Czech Republic', 1, 2, DATE_SUB(NOW(), INTERVAL 1 DAY), 5, 420, true, 2, 1000.00),
('session_002', '192.168.1.101', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)', 'https://facebook.com', 'social', 'Czech Republic', 'CZ', 'Brno', 'Brno', 49.1951, 16.6068, 'Europe/Prague', 'T-Mobile Czech Republic', 1, 3, DATE_SUB(NOW(), INTERVAL 1 DAY), 3, 180, false, 0, 0.00),
('session_003', '10.0.0.50', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'https://twitter.com', 'social', 'Slovakia', 'SK', 'Bratislava', 'Bratislava', 48.1486, 17.1077, 'Europe/Bratislava', 'Slovak Telekom', 2, null, DATE_SUB(NOW(), INTERVAL 2 DAYS), 8, 650, true, 1, 1200.00),
('session_004', '203.0.113.45', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0)', 'https://google.com', 'organic', 'Germany', 'DE', 'Bavaria', 'Munich', 48.1351, 11.5820, 'Europe/Berlin', 'Deutsche Telekom', 3, null, DATE_SUB(NOW(), INTERVAL 2 DAYS), 4, 290, true, 1, 800.00),
('session_005', '172.16.0.25', 'Mozilla/5.0 (X11; Linux x86_64)', 'direct', 'direct', 'Czech Republic', 'CZ', 'Ostrava', 'Ostrava', 49.8209, 18.2625, 'Europe/Prague', 'UPC Czech Republic', 1, 4, DATE_SUB(NOW(), INTERVAL 3 DAYS), 2, 95, false, 0, 0.00),

-- Week 2 data
('session_006', '198.51.100.30', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://instagram.com', 'social', 'Austria', 'AT', 'Vienna', 'Wien', 48.2082, 16.3738, 'Europe/Vienna', 'A1 Telekom Austria', 2, null, DATE_SUB(NOW(), INTERVAL 8 DAYS), 6, 480, true, 2, 1400.00),
('session_007', '203.0.113.78', 'Mozilla/5.0 (Android 11; Mobile)', 'https://linkedin.com', 'social', 'Poland', 'PL', 'Krakow', 'Kraków', 50.0647, 19.9450, 'Europe/Warsaw', 'Orange Polska', 3, null, DATE_SUB(NOW(), INTERVAL 9 DAYS), 3, 210, false, 0, 0.00),
('session_008', '192.168.2.15', 'Mozilla/5.0 (iPad; CPU OS 14_0 like Mac OS X)', 'https://youtube.com', 'social', 'Czech Republic', 'CZ', 'Plzen', 'Plzeň', 49.7384, 13.3736, 'Europe/Prague', 'Vodafone Czech Republic', 4, null, DATE_SUB(NOW(), INTERVAL 10 DAYS), 7, 590, true, 3, 1800.00),
('session_009', '10.1.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://google.com', 'organic', 'Czech Republic', 'CZ', 'Ceske Budejovice', 'České Budějovice', 48.9744, 14.4742, 'Europe/Prague', 'CETIN', 5, null, DATE_SUB(NOW(), INTERVAL 11 DAYS), 4, 320, true, 1, 600.00),
('session_010', '172.20.0.40', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X)', 'direct', 'direct', 'Slovakia', 'SK', 'Košice', 'Košice', 48.7164, 21.2611, 'Europe/Bratislava', 'Orange Slovensko', 1, null, DATE_SUB(NOW(), INTERVAL 12 DAYS), 5, 380, false, 0, 0.00),

-- Week 3 data
('session_011', '198.51.100.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://bing.com', 'organic', 'Germany', 'DE', 'Berlin', 'Berlin', 52.5200, 13.4050, 'Europe/Berlin', 'Vodafone Germany', 2, null, DATE_SUB(NOW(), INTERVAL 15 DAYS), 6, 450, true, 2, 1600.00),
('session_012', '203.0.113.200', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'https://reddit.com', 'social', 'Austria', 'AT', 'Salzburg', 'Salzburg', 47.8095, 13.0550, 'Europe/Vienna', 'Magenta Telekom', 3, null, DATE_SUB(NOW(), INTERVAL 16 DAYS), 4, 280, false, 0, 0.00),
('session_013', '192.168.3.75', 'Mozilla/5.0 (Android 12; Mobile)', 'https://tiktok.com', 'social', 'Czech Republic', 'CZ', 'Olomouc', 'Olomouc', 49.5938, 17.2509, 'Europe/Prague', 'T-Mobile Czech Republic', 4, null, DATE_SUB(NOW(), INTERVAL 17 DAYS), 9, 720, true, 4, 2200.00),
('session_014', '10.2.2.50', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:92.0)', 'https://duckduckgo.com', 'organic', 'Poland', 'PL', 'Warsaw', 'Warszawa', 52.2297, 21.0122, 'Europe/Warsaw', 'Play', 5, null, DATE_SUB(NOW(), INTERVAL 18 DAYS), 3, 195, true, 1, 500.00),
('session_015', '172.25.0.80', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7 like Mac OS X)', 'direct', 'direct', 'Czech Republic', 'CZ', 'Liberec', 'Liberec', 50.7663, 15.0543, 'Europe/Prague', 'O2 Czech Republic', 1, null, DATE_SUB(NOW(), INTERVAL 19 DAYS), 2, 120, false, 0, 0.00),

-- Week 4 data
('session_016', '198.51.100.150', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://yahoo.com', 'organic', 'Slovakia', 'SK', 'Zilina', 'Žilina', 49.2231, 18.7395, 'Europe/Bratislava', 'Slovak Telekom', 2, null, DATE_SUB(NOW(), INTERVAL 22 DAYS), 5, 410, true, 2, 1300.00),
('session_017', '203.0.113.250', 'Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X)', 'https://pinterest.com', 'social', 'Germany', 'DE', 'Hamburg', 'Hamburg', 53.5511, 9.9937, 'Europe/Berlin', 'Telefónica Germany', 3, null, DATE_SUB(NOW(), INTERVAL 23 DAYS), 7, 560, false, 0, 0.00),
('session_018', '192.168.4.25', 'Mozilla/5.0 (Android 11; Mobile)', 'https://snapchat.com', 'social', 'Austria', 'AT', 'Innsbruck', 'Innsbruck', 47.2692, 11.4041, 'Europe/Vienna', 'Drei Austria', 4, null, DATE_SUB(NOW(), INTERVAL 24 DAYS), 8, 640, true, 3, 1900.00),
('session_019', '10.3.3.75', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'https://ecosia.org', 'organic', 'Czech Republic', 'CZ', 'Hradec Kralove', 'Hradec Králové', 50.2092, 15.8327, 'Europe/Prague', 'UPC Czech Republic', 5, null, DATE_SUB(NOW(), INTERVAL 25 DAYS), 4, 310, true, 1, 650.00),
('session_020', '172.30.0.100', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_1 like Mac OS X)', 'direct', 'direct', 'Poland', 'PL', 'Gdansk', 'Gdańsk', 54.3520, 18.6466, 'Europe/Warsaw', 'Plus', 1, null, DATE_SUB(NOW(), INTERVAL 26 DAYS), 6, 480, false, 0, 0.00),

-- Additional high-converting sessions for better analytics
('session_021', '198.51.100.200', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://google.com', 'organic', 'Czech Republic', 'CZ', 'Praha', 'Praha', 50.0755, 14.4378, 'Europe/Prague', 'O2 Czech Republic', 1, null, DATE_SUB(NOW(), INTERVAL 5 DAYS), 12, 900, true, 5, 2500.00),
('session_022', '203.0.113.300', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'https://facebook.com', 'social', 'Germany', 'DE', 'Frankfurt', 'Frankfurt am Main', 50.1109, 8.6821, 'Europe/Berlin', 'Deutsche Telekom', 2, null, DATE_SUB(NOW(), INTERVAL 6 DAYS), 10, 780, true, 4, 2100.00),
('session_023', '192.168.5.50', 'Mozilla/5.0 (Android 12; Mobile)', 'https://instagram.com', 'social', 'Austria', 'AT', 'Graz', 'Graz', 47.0707, 15.4395, 'Europe/Vienna', 'A1 Telekom Austria', 3, null, DATE_SUB(NOW(), INTERVAL 7 DAYS), 8, 620, true, 3, 1750.00),
('session_024', '10.4.4.25', 'Mozilla/5.0 (iPad; CPU OS 15_1 like Mac OS X)', 'direct', 'direct', 'Slovakia', 'SK', 'Presov', 'Prešov', 49.0008, 21.2393, 'Europe/Bratislava', 'Orange Slovensko', 4, null, DATE_SUB(NOW(), INTERVAL 13 DAYS), 6, 450, true, 2, 1100.00),
('session_025', '172.35.0.125', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_2 like Mac OS X)', 'https://twitter.com', 'social', 'Czech Republic', 'CZ', 'Brno', 'Brno', 49.1951, 16.6068, 'Europe/Prague', 'Vodafone Czech Republic', 5, null, DATE_SUB(NOW(), INTERVAL 14 DAYS), 7, 540, true, 2, 1200.00);

-- Insert real-time stats data for the last 24 hours (one entry per hour)
INSERT INTO real_time_stats (
    event_id, timestamp, active_visitors, sales_last_minute, sales_last_hour,
    revenue_last_minute, revenue_last_hour, conversion_rate, bounce_rate,
    average_session_duration, pages_per_session
) VALUES
-- Recent hours with varying activity
(1, DATE_SUB(NOW(), INTERVAL 1 HOUR), 25, 2, 8, 1000.00, 4200.00, 15.5, 35.2, 385.5, 4.2),
(1, DATE_SUB(NOW(), INTERVAL 2 HOURS), 30, 1, 6, 500.00, 3100.00, 18.2, 28.7, 420.3, 4.8),
(1, DATE_SUB(NOW(), INTERVAL 3 HOURS), 22, 3, 12, 1500.00, 6500.00, 22.1, 31.5, 395.7, 4.5),
(1, DATE_SUB(NOW(), INTERVAL 4 HOURS), 18, 0, 4, 0.00, 2000.00, 12.8, 42.3, 285.2, 3.1),
(1, DATE_SUB(NOW(), INTERVAL 5 HOURS), 35, 4, 15, 2000.00, 7800.00, 25.7, 25.8, 465.8, 5.2),
(1, DATE_SUB(NOW(), INTERVAL 6 HOURS), 28, 2, 9, 1200.00, 4700.00, 19.3, 33.1, 398.4, 4.3),

-- Event 2 stats
(2, DATE_SUB(NOW(), INTERVAL 1 HOUR), 45, 3, 18, 3600.00, 21600.00, 28.5, 22.1, 520.7, 6.1),
(2, DATE_SUB(NOW(), INTERVAL 2 HOURS), 52, 5, 25, 6000.00, 30000.00, 32.8, 18.9, 580.3, 6.8),
(2, DATE_SUB(NOW(), INTERVAL 3 HOURS), 38, 2, 14, 2400.00, 16800.00, 24.7, 26.3, 445.2, 5.4),
(2, DATE_SUB(NOW(), INTERVAL 4 HOURS), 41, 4, 20, 4800.00, 24000.00, 29.1, 21.5, 505.8, 5.9),

-- Event 3 stats  
(3, DATE_SUB(NOW(), INTERVAL 1 HOUR), 15, 1, 5, 800.00, 4000.00, 16.7, 38.5, 325.4, 3.8),
(3, DATE_SUB(NOW(), INTERVAL 2 HOURS), 20, 2, 8, 1600.00, 6400.00, 21.3, 32.7, 385.9, 4.2),
(3, DATE_SUB(NOW(), INTERVAL 3 HOURS), 12, 0, 3, 0.00, 2400.00, 11.8, 45.2, 255.7, 2.9),

-- Event 4 stats
(4, DATE_SUB(NOW(), INTERVAL 1 HOUR), 60, 6, 35, 4200.00, 24500.00, 35.2, 15.8, 650.3, 7.2),
(4, DATE_SUB(NOW(), INTERVAL 2 HOURS), 75, 8, 42, 5600.00, 29400.00, 38.9, 12.4, 720.5, 8.1),
(4, DATE_SUB(NOW(), INTERVAL 3 HOURS), 55, 4, 28, 2800.00, 19600.00, 31.7, 18.2, 595.8, 6.8),

-- Event 5 stats
(5, DATE_SUB(NOW(), INTERVAL 1 HOUR), 8, 0, 2, 0.00, 1200.00, 8.5, 52.3, 185.2, 2.1),
(5, DATE_SUB(NOW(), INTERVAL 2 HOURS), 12, 1, 4, 600.00, 2400.00, 12.1, 48.7, 225.8, 2.6),
(5, DATE_SUB(NOW(), INTERVAL 3 HOURS), 6, 0, 1, 0.00, 600.00, 5.2, 58.9, 145.3, 1.8);

-- Add more visitor sessions to simulate sustained traffic over the past month
INSERT INTO visitor_sessions (
    session_id, ip_address, user_agent, referer, traffic_source, 
    country, country_code, region, city, event_id, timestamp, 
    page_views, duration_seconds, converted, tickets_purchased, revenue_generated
) 
SELECT 
    CONCAT('bulk_session_', LPAD(seq, 4, '0')) as session_id,
    CONCAT('192.168.', FLOOR(1 + RAND() * 10), '.', FLOOR(1 + RAND() * 254)) as ip_address,
    'Mozilla/5.0 (compatible; bulk data)' as user_agent,
    CASE FLOOR(RAND() * 6)
        WHEN 0 THEN 'https://google.com'
        WHEN 1 THEN 'https://facebook.com'
        WHEN 2 THEN 'https://instagram.com'
        WHEN 3 THEN 'direct'
        WHEN 4 THEN 'https://twitter.com'
        ELSE 'https://linkedin.com'
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
    CASE FLOOR(RAND() * 6)
        WHEN 0 THEN 'Praha'
        WHEN 1 THEN 'Brno'
        WHEN 2 THEN 'Bratislava'
        WHEN 3 THEN 'Berlin'
        WHEN 4 THEN 'Vienna'
        ELSE 'Warsaw'
    END as city,
    FLOOR(1 + RAND() * 5) as event_id,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as timestamp,
    FLOOR(1 + RAND() * 10) as page_views,
    FLOOR(30 + RAND() * 600) as duration_seconds,
    CASE WHEN RAND() < 0.25 THEN true ELSE false END as converted,
    CASE WHEN RAND() < 0.25 THEN FLOOR(1 + RAND() * 3) ELSE 0 END as tickets_purchased,
    CASE WHEN RAND() < 0.25 THEN ROUND(500 + RAND() * 1500, 2) ELSE 0.00 END as revenue_generated
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