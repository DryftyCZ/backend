-- Analytics and visitor tracking tables

-- Table for IP processing queue (batch geolocation)
CREATE TABLE IF NOT EXISTS ip_processing_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL UNIQUE,
    visitor_session_id BIGINT,
    processing_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'SKIPPED') NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME,
    retry_count INT DEFAULT 0,
    last_error TEXT,
    priority INT DEFAULT 5,
    
    -- Geolocation data (filled after processing)
    country VARCHAR(100),
    country_code VARCHAR(2),
    region VARCHAR(100),
    city VARCHAR(100),
    latitude DOUBLE,
    longitude DOUBLE,
    timezone VARCHAR(50),
    isp VARCHAR(255),
    
    INDEX idx_ip_status (processing_status),
    INDEX idx_ip_created (created_at),
    INDEX idx_ip_attempts (retry_count),
    INDEX idx_ip_priority (priority)
);

-- Analytics and visitor tracking tables

-- Table for tracking visitor sessions
CREATE TABLE IF NOT EXISTS visitor_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    ip_address VARCHAR(45) NOT NULL,
    user_agent TEXT,
    referer TEXT,
    traffic_source VARCHAR(100),
    country VARCHAR(100),
    country_code VARCHAR(2),
    region VARCHAR(100),
    city VARCHAR(100),
    latitude DOUBLE,
    longitude DOUBLE,
    timezone VARCHAR(50),
    isp VARCHAR(255),
    event_id BIGINT,
    user_id BIGINT,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    page_views INT DEFAULT 1,
    duration_seconds BIGINT,
    converted BOOLEAN DEFAULT FALSE,
    tickets_purchased INT DEFAULT 0,
    revenue_generated DECIMAL(10,2) DEFAULT 0.00,
    
    INDEX idx_visitor_ip (ip_address),
    INDEX idx_visitor_timestamp (timestamp),
    INDEX idx_visitor_event (event_id),
    INDEX idx_visitor_country (country_code),
    INDEX idx_visitor_traffic_source (traffic_source),
    
    CONSTRAINT fk_visitor_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE SET NULL,
    CONSTRAINT fk_visitor_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Table for real-time statistics
CREATE TABLE IF NOT EXISTS real_time_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active_visitors INT DEFAULT 0,
    sales_last_minute INT DEFAULT 0,
    sales_last_hour INT DEFAULT 0,
    revenue_last_minute DECIMAL(10,2) DEFAULT 0.00,
    revenue_last_hour DECIMAL(10,2) DEFAULT 0.00,
    conversion_rate DOUBLE DEFAULT 0.0,
    bounce_rate DOUBLE DEFAULT 0.0,
    average_session_duration DOUBLE DEFAULT 0.0,
    pages_per_session DOUBLE DEFAULT 0.0,
    
    INDEX idx_realtime_timestamp (timestamp),
    INDEX idx_realtime_event (event_id),
    
    CONSTRAINT fk_realtime_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE SET NULL
);

-- Update tickets table to support better analytics
ALTER TABLE tickets 
ADD COLUMN IF NOT EXISTS purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45),
ADD COLUMN IF NOT EXISTS country VARCHAR(100),
ADD COLUMN IF NOT EXISTS session_id VARCHAR(255);

-- Add indexes for better performance
CREATE INDEX IF NOT EXISTS idx_tickets_purchase_date ON tickets(purchase_date);
CREATE INDEX IF NOT EXISTS idx_tickets_ip_address ON tickets(ip_address);
CREATE INDEX IF NOT EXISTS idx_tickets_session_id ON tickets(session_id);

-- Create view for dashboard analytics
CREATE OR REPLACE VIEW dashboard_analytics AS
SELECT 
    COUNT(DISTINCT vs.session_id) as total_visitors,
    COUNT(DISTINCT vs.country) as total_countries,
    COUNT(vs.id) as total_pageviews,
    COUNT(CASE WHEN vs.converted = TRUE THEN 1 END) as total_conversions,
    SUM(vs.tickets_purchased) as total_tickets_sold,
    SUM(vs.revenue_generated) as total_revenue,
    AVG(vs.duration_seconds) as avg_session_duration,
    AVG(vs.page_views) as avg_pages_per_session,
    (COUNT(CASE WHEN vs.converted = TRUE THEN 1 END) * 100.0 / COUNT(DISTINCT vs.session_id)) as conversion_rate,
    DATE(vs.timestamp) as date
FROM visitor_sessions vs
WHERE vs.timestamp >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY DATE(vs.timestamp)
ORDER BY date DESC;