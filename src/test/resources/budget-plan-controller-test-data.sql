/*
Set up users
*/
INSERT INTO accounts ( email, password, status) VALUES
    ('organizer@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    ('owner@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    ('admin@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    ('regular@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    ('organizer2@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE');

INSERT INTO users ( first_name, last_name, profile_picture_url, user_role, account_id) VALUES
    ( 'John Organizer', 'Doe', 'https://example.com/johndoe.jpg', 1, 1),
    ('Marko Owner', 'Petrović', 'https://example.com/marko.jpg', 2, 2),
    ('Ana Admin', 'Jovanović', 'https://example.com/ana.jpg', 3, 3),
    ('Tamara Regular user', 'Marković', 'https://example.com/ana.jpg', 0, 4),
    ('Tamara Organizer 2', 'Organization', 'https://example.com/ana.jpg', 1, 5);

UPDATE accounts SET user_id = 1 WHERE email = 'organizer@example.com';
UPDATE accounts SET user_id = 2 WHERE email = 'owner@example.com';
UPDATE accounts SET user_id = 3 WHERE email = 'admin@example.com';
UPDATE accounts SET user_id = 4 WHERE email = 'regular@example.com';
UPDATE accounts SET user_id = 5 WHERE email = 'organizer2@example.com';

INSERT INTO event_organizers (id, country, city, address, latitude, longitude, phone_number)
VALUES
    (1, 'Serbia', 'Belgrade', 'Bulevar kralja Aleksandra 71', 44, 20, '+381601234567'),
    (5, 'Serbia', 'Belgrade', 'Bulevar kralja Aleksandra 72', 44, 20, '+381601543545');

INSERT INTO owners (id, company_name, country, city, address, latitude, longitude, contact_phone, description)
VALUES
    (2, 'Marko Catering', 'Srbija', 'Beograd', 'Knez Mihailova 12', 44, 20, '+381641114567', 'Najbolja Marko ketering usluga u Beogradu.');

INSERT INTO administrators (id) VALUES (3);

-- Event types
INSERT INTO event_types (description, name, is_active)
VALUES
    ('Corporate Event', 'Conference', true),
    ('Music and Entertainment Event', 'Concert', true);

-- Insert events
INSERT INTO events ( event_type_id, name, description, max_participants, privacy_type, start_date, end_date, start_time, end_time, country, city, address, latitude, longitude, organizer_id, created_date) VALUES
    ( 1, 'Tech Conference 2024', 'A conference about technology', 200, 0, '2024-12-16', '2024-12-16', '09:00', '14:00', 'Serbia', 'Belgrade', 'Bulevar Kralja Aleksandra 10', 44, 20, 1, CURRENT_TIMESTAMP),
    ( 2, 'Marko''s Wedding', 'Wedding celebration for Marko and Jelena', 150, 0, '2024-12-19', '2024-12-19', '18:00', '22:00', 'Serbia', 'Niš', 'Njegoševa 5', 43, 21, 1, CURRENT_TIMESTAMP),
    ( 1, 'Tech Conference 2025', 'A conference about technology', 200, 0, '2025-12-16', '2025-12-16', '09:00', '14:00', 'Serbia', 'Belgrade', 'Bulevar Kralja Aleksandra 10', 44, 20, 5, CURRENT_TIMESTAMP);

-- Insert Offering Categories
INSERT INTO offering_type ( id,name, description, status)
VALUES
    (1,'Catering Services', 'Catering services for various event types such as weddings, corporate events, and more.', 'ACCEPTED'),
    (2, 'Technical Services', 'Technical services including sound systems, lighting, and AV equipment for events.', 'ACCEPTED'),
    (3, 'Technical Services 2', 'Technical services including sound systems, lighting, and AV equipment for events.', 'ACCEPTED');

-- offering type for event type
INSERT INTO event_types_offering_categories (event_type_id, offering_categories_id)
VALUES (
           1,1
       );

-- Insert into offerings table
INSERT INTO offerings ( name,deleted, description, price, discount, is_visible, is_available, status, offering_category_id, owner_id)
VALUES
    ( 'Corporate Event Planning',FALSE, 'End-to-end planning for corporate events including venue selection, scheduling, and coordination.', 5000.0, 10.0, true, true, 0, 1, 2),
    ( 'Wedding Catering',FALSE, 'Full-service catering for weddings including appetizers, main courses, and desserts.', 3000.0, 15.0, true, true, 0, 2, 2),
    ( 'Live Band Performance',FALSE, 'Live music performance for weddings and concerts.', 2000.0, 5.0, true, true, 0, 2, 2),
    ( 'Corporate Event Planning 2',FALSE, 'End-to-end planning for corporate events including venue selection, scheduling, and coordination.', 5000.0, 10.0, true, false, 0, 1, 2),
    ( 'Corporate Event Planning 3',FALSE, 'End-to-end planning for corporate events including venue selection, scheduling, and coordination.', 5000.0, 10.0, true, false, 0, 1, 2);

-- Insert into services table
INSERT INTO service (id, specifics, duration, reservation_deadline, cancellation_deadline, reservation_type,minimum_arrangement,maximum_arrangement)
VALUES
    (1, 'Full event planning with vendor coordination', 2, 48, 24, 'AUTOMATIC',0,0),
    (2, 'Catering services for 100 guests', 2, 72, 48, 'AUTOMATIC',0,0);

INSERT INTO product (id)
VALUES
    (3),
    (4),
    (5);

INSERT INTO budget_item (amount, currency, offering_category_id, event_id)
VALUES (250, 'EUR', 1, 1),
       (250, 'EUR', 3, 1);

INSERT INTO purchases (end_date, end_time, amount, currency, start_date, start_time, event_id, offering_id)
VALUES (
           '2025-08-10',
           '18:00:00',
           100.00,
           'EUR',
           '2025-08-05',
           '09:00:00',
           1,
           5
       );