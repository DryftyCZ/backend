-- Create organization_settings table
CREATE TABLE organization_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organization_name VARCHAR(255) NOT NULL DEFAULT 'SmartTicket s.r.o.',
    organization_email VARCHAR(255) NOT NULL DEFAULT 'info@smartticket.cz',
    language VARCHAR(10) NOT NULL DEFAULT 'cs',
    currency VARCHAR(10) NOT NULL DEFAULT 'CZK',
    timezone VARCHAR(50) NOT NULL DEFAULT 'Europe/Prague',
    date_format VARCHAR(20) NOT NULL DEFAULT 'DD.MM.YYYY',
    ticket_prefix VARCHAR(10) NOT NULL DEFAULT 'ST',
    enable_email_notifications BOOLEAN DEFAULT true,
    enable_sms_notifications BOOLEAN DEFAULT false,
    enable_auto_backup BOOLEAN DEFAULT true,
    backup_frequency VARCHAR(20) DEFAULT 'daily',
    maintenance_mode BOOLEAN DEFAULT false
);

-- Insert default organization settings
INSERT INTO organization_settings 
(organization_name, organization_email, language, currency, timezone, date_format, ticket_prefix, enable_email_notifications, enable_sms_notifications, enable_auto_backup, backup_frequency, maintenance_mode)
VALUES 
('SmartTicket s.r.o.', 'info@smartticket.cz', 'cs', 'CZK', 'Europe/Prague', 'DD.MM.YYYY', 'ST', true, false, true, 'daily', false);