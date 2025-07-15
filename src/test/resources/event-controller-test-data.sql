INSERT INTO accounts (id, email, password, status) VALUES
    (1, 'organizer@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    (2, 'jana@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE');

INSERT INTO users (id, first_name, last_name, user_role, account_id) VALUES
    (1, 'John', 'Doe', 1, 1),
    (2, 'Jana', 'JA', 0, 2);

UPDATE accounts SET user_id = 1 WHERE id = 1;
UPDATE accounts SET user_id = 2 WHERE id = 2;

INSERT INTO event_organizers (id, country, city, address, latitude, longitude, phone_number)
VALUES
    (1, 'Serbia', 'Belgrade', 'Bulevar kralja Aleksandra 73', 44, 20, '+381601234567');

INSERT INTO event_types (id, description, name, is_active) VALUES
    (1, 'Corporate Event', 'Conference', true),
    (2, 'Music and Entertainment Event', 'Concert', true);

INSERT INTO events ( id, event_type_id, name, description, max_participants, privacy_type, start_date, end_date, start_time, end_time, country, city, address, latitude, longitude, organizer_id, created_date) VALUES
    ( 1, 1, 'Tech Conference 2024', 'A conference about technology', 200, 0, '2024-12-16', '2024-12-16', '09:00', '14:00', 'Serbia', 'Belgrade', 'Bulevar Kralja Aleksandra 10', 44, 20, 1, CURRENT_TIMESTAMP),
    ( 2, 1, 'Rock Concert', 'Live rock concert in Belgrade', 1, 1, '2024-12-17', '2024-12-17', '20:00', '23:30', 'Serbia', 'Belgrade', 'Bulevar Kralja Aleksandra 10', 44, 20, 1, CURRENT_TIMESTAMP);

INSERT INTO activities (id, name, description, start_time, end_time, location, event_id) VALUES
    (10, 'Opening ceremony', 'The starting activity of the event', '2024-12-16 10:00', '2024-12-16 12:00', 'Conference hall', 1);

ALTER TABLE events ALTER COLUMN id RESTART WITH 3;
ALTER TABLE activities ALTER COLUMN id RESTART WITH 20;
