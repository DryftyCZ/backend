-- Quick Sample Data Script
-- Run this in H2 Console if you need more sample data immediately
-- Access H2 Console at: http://localhost:8080/h2-console
-- JDBC URL: jdbc:h2:file:./data/ticketing-db
-- Username: sa, Password: password

-- Check current data counts
SELECT 
    'Events' as table_name, COUNT(*) as count FROM events
UNION ALL
SELECT 
    'Tickets' as table_name, COUNT(*) as count FROM tickets  
UNION ALL
SELECT 
    'Visitor Sessions' as table_name, COUNT(*) as count FROM visitor_sessions
UNION ALL
SELECT 
    'Real Time Stats' as table_name, COUNT(*) as count FROM real_time_stats;

-- Quick insert for more visitor sessions (run if needed)
INSERT INTO visitor_sessions (
    session_id, ip_address, user_agent, referer, traffic_source,
    country, country_code, region, city, event_id, timestamp,
    page_views, duration_seconds, converted, tickets_purchased, revenue_generated
) VALUES 
('quick_session_1', '203.0.113.10', 'Mozilla/5.0', 'https://google.com', 'organic', 
 'Czech Republic', 'CZ', 'Prague', 'Praha', 1, NOW() - INTERVAL '2' HOUR, 
 4, 300, true, 1, 500.00),
('quick_session_2', '203.0.113.11', 'Mozilla/5.0', 'https://facebook.com', 'social',
 'Slovakia', 'SK', 'Bratislava', 'Bratislava', 2, NOW() - INTERVAL '1' HOUR,
 6, 450, true, 2, 1200.00),
('quick_session_3', '203.0.113.12', 'Mozilla/5.0', 'direct', 'direct',
 'Germany', 'DE', 'Berlin', 'Berlin', 3, NOW() - INTERVAL '30' MINUTE,
 3, 180, false, 0, 0.00);

-- Quick real-time stats (run if needed)
INSERT INTO real_time_stats (
    event_id, timestamp, active_visitors, sales_last_minute, sales_last_hour,
    revenue_last_minute, revenue_last_hour, conversion_rate, bounce_rate,
    average_session_duration, pages_per_session
) VALUES 
(1, NOW(), 42, 3, 15, 1500.00, 7500.00, 24.5, 28.3, 385.2, 4.7),
(2, NOW(), 35, 2, 12, 2400.00, 14400.00, 31.8, 22.1, 465.8, 5.3),
(3, NOW(), 28, 1, 8, 800.00, 6400.00, 18.7, 35.6, 325.4, 3.9);

-- Update some tickets to be more recent (run if needed)
UPDATE tickets 
SET purchase_date = NOW() - INTERVAL FLOOR(RAND() * 7) DAY,
    country = CASE FLOOR(RAND() * 3) 
        WHEN 0 THEN 'Czech Republic'
        WHEN 1 THEN 'Slovakia' 
        ELSE 'Germany'
    END,
    ip_address = CONCAT('192.168.', FLOOR(1 + RAND() * 10), '.', FLOOR(1 + RAND() * 254))
WHERE id IN (SELECT id FROM tickets LIMIT 20);

-- Verify sample data with analytics queries
SELECT 
    'Total Visitors (Last 30 Days)' as metric,
    COUNT(DISTINCT session_id) as value
FROM visitor_sessions 
WHERE timestamp >= NOW() - INTERVAL '30' DAY

UNION ALL

SELECT 
    'Total Revenue (Last 30 Days)' as metric,
    ROUND(SUM(revenue_generated), 2) as value
FROM visitor_sessions 
WHERE timestamp >= NOW() - INTERVAL '30' DAY

UNION ALL

SELECT 
    'Conversion Rate (Last 30 Days)' as metric,
    ROUND(COUNT(CASE WHEN converted = true THEN 1 END) * 100.0 / COUNT(*), 2) as value
FROM visitor_sessions 
WHERE timestamp >= NOW() - INTERVAL '30' DAY

UNION ALL

SELECT 
    'Countries Represented' as metric,
    COUNT(DISTINCT country) as value  
FROM visitor_sessions

UNION ALL

SELECT 
    'Active Visitors (Last Hour)' as metric,
    COUNT(DISTINCT session_id) as value
FROM visitor_sessions 
WHERE timestamp >= NOW() - INTERVAL '1' HOUR;

-- Geographic breakdown
SELECT 
    country,
    COUNT(*) as visitors,
    COUNT(CASE WHEN converted = true THEN 1 END) as conversions,
    ROUND(SUM(revenue_generated), 2) as revenue
FROM visitor_sessions 
GROUP BY country 
ORDER BY visitors DESC;

-- Traffic source analysis
SELECT 
    traffic_source,
    COUNT(*) as visitors,
    COUNT(CASE WHEN converted = true THEN 1 END) as conversions,
    ROUND(COUNT(CASE WHEN converted = true THEN 1 END) * 100.0 / COUNT(*), 2) as conversion_rate
FROM visitor_sessions 
GROUP BY traffic_source 
ORDER BY visitors DESC;

-- Daily visitor trends (last 7 days)
SELECT 
    DATE(timestamp) as date,
    COUNT(DISTINCT session_id) as unique_visitors,
    COUNT(*) as total_sessions,
    ROUND(SUM(revenue_generated), 2) as revenue
FROM visitor_sessions 
WHERE timestamp >= NOW() - INTERVAL '7' DAY
GROUP BY DATE(timestamp) 
ORDER BY date DESC;