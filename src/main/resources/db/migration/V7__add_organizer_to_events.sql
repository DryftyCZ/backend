-- Add organizer_id column to events table
ALTER TABLE events ADD COLUMN organizer_id BIGINT;

-- Add foreign key constraint
ALTER TABLE events ADD CONSTRAINT fk_event_organizer 
    FOREIGN KEY (organizer_id) REFERENCES users(id);

-- Create index for better performance when querying by organizer
CREATE INDEX idx_events_organizer_id ON events(organizer_id);

-- Update existing events to set the first admin as organizer (temporary solution)
UPDATE events 
SET organizer_id = (
    SELECT u.id 
    FROM users u 
    JOIN user_roles ur ON u.id = ur.user_id 
    WHERE ur.role_name = 'ADMIN' 
    LIMIT 1
)
WHERE organizer_id IS NULL;

-- Now make the column NOT NULL after populating existing data
ALTER TABLE events ALTER COLUMN organizer_id SET NOT NULL;