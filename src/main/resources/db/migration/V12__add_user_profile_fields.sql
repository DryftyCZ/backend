-- Add profile fields to users table
ALTER TABLE users 
ADD COLUMN first_name VARCHAR(255),
ADD COLUMN last_name VARCHAR(255),
ADD COLUMN phone VARCHAR(50),
ADD COLUMN position VARCHAR(255),
ADD COLUMN department VARCHAR(255),
ADD COLUMN bio TEXT,
ADD COLUMN language VARCHAR(10) DEFAULT 'cs',
ADD COLUMN timezone VARCHAR(50) DEFAULT 'Europe/Prague',
ADD COLUMN theme VARCHAR(20) DEFAULT 'light',
ADD COLUMN email_notifications BOOLEAN DEFAULT true,
ADD COLUMN push_notifications BOOLEAN DEFAULT true,
ADD COLUMN weekly_reports BOOLEAN DEFAULT false;

-- Update existing test user with some profile data
UPDATE users 
SET first_name = 'Jan', 
    last_name = 'Novák', 
    position = 'Administrator', 
    department = 'IT'
WHERE username = 'admin';