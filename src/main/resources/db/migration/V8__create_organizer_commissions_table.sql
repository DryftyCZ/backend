-- Create organizer commissions table
CREATE TABLE organizer_commissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organizer_id BIGINT NOT NULL UNIQUE,
    commission_percentage DECIMAL(5,2) NOT NULL DEFAULT 20.00,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_organizer_commission_user 
        FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create index for better performance
CREATE INDEX idx_organizer_commissions_organizer_id ON organizer_commissions(organizer_id);
CREATE INDEX idx_organizer_commissions_active ON organizer_commissions(is_active);

-- Insert default commissions for existing organizers
INSERT INTO organizer_commissions (organizer_id, commission_percentage, is_active)
SELECT u.id, 20.00, TRUE 
FROM users u 
JOIN user_roles ur ON u.id = ur.user_id 
WHERE ur.role_name = 'ORGANIZER' 
AND NOT EXISTS (SELECT 1 FROM organizer_commissions oc WHERE oc.organizer_id = u.id);