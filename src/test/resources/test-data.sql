INSERT INTO accounts ( email, password, status) VALUES
                                                    ('account1@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE');
INSERT INTO users ( first_name, last_name, profile_picture_url, user_role, account_id)
VALUES
    ( 'John', 'Doe', 'https://example.com/johndoe.jpg', 1, 1);

INSERT INTO event_organizers (id, country, city, address, latitude, longitude, phone_number)
VALUES
    (1, 'Serbia', 'Beograd', 'Bulevar kralja Aleksandra 73', 44, 20, '+381601234567');

-- Insert event types
INSERT INTO event_types (description, name, is_active)
VALUES
    ('Corporate Event', 'Conference', true),
    ('Music and Entertainment Event', 'Concert', true);

-- Insert events
INSERT INTO events ( event_type_id, name, description, max_participants, privacy_type, start_date, end_date, start_time, end_time, country, city, address, latitude, longitude, organizer_id, created_date) VALUES
( 1, 'Tech Conference 2024', 'A conference about technology', 200, 0, '2024-12-16', '2024-12-16', '09:00', '14:00', 'Serbia', 'Belgrade', 'Bulevar Kralja Aleksandra 10', 44, 20, 1, CURRENT_TIMESTAMP),
( 1, 'Rock Concert', 'Live rock concert in Belgrade', 1000, 0, '2024-12-17', '2024-12-17', '20:00', '23:30', 'Serbia', 'Belgrade', 'Bulevar Kralja Aleksandra 10', 44, 20, 1, CURRENT_TIMESTAMP),
( 2, 'Football Match', 'Exciting football match between two rivals', 5000, 0, '2024-12-18', '2024-12-18', '17:00', '19:00', 'Serbia', 'Novi Sad', 'Trg Slobode 5', 45, 19, 1, CURRENT_TIMESTAMP),
( 2, 'Marko''s Wedding', 'Wedding celebration for Marko and Jelena', 150, 1, '2024-12-19', '2024-12-19', '18:00', '22:00', 'Serbia', 'Niš', 'Njegoševa 5', 43, 21, 1, CURRENT_TIMESTAMP);
-- Insert Accounts
INSERT INTO accounts ( email, password, status)
VALUES
    ('marko.p@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    ('ana.j@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE');


-- Insert Users
INSERT INTO users ( first_name, last_name, profile_picture_url, user_role, account_id)
VALUES
    ('Marko', 'Petrović', 'https://example.com/marko.jpg', 2, 2),
    ('Ana', 'Jovanović', 'https://example.com/ana.jpg', 2, 3);


-- Insert Owners (References Users)
INSERT INTO owners (id, company_name, country, city, address, latitude, longitude, contact_phone, description)
VALUES
    (2, 'Petrović Catering', 'Srbija', 'Beograd', 'Knez Mihailova 12', 44, 20, '+381641114567', 'Najbolja ketering usluga u Beogradu.'),
    (3, 'Jovanović Event Studio', 'Srbija', 'Novi Sad', 'Zmaj Jovina 10', 45, 19, '+381631114500', 'Planiranje događaja i dekoracija prostora.');
-- Insert Offering Categories
INSERT INTO offering_type ( id,name, description, status)
VALUES
    (1,'Catering Services', 'Catering services for various event types such as weddings, corporate events, and more.', 'ACCEPTED'),
    (2, 'Technical Services', 'Technical services including sound systems, lighting, and AV equipment for events.', 'ACCEPTED');


-- Insert into offerings table
INSERT INTO offerings ( name,deleted, description, price, discount, is_visible, is_available, status, offering_category_id, owner_id)
VALUES
    ( 'Corporate Event Planning',FALSE, 'End-to-end planning for corporate events including venue selection, scheduling, and coordination.', 5000.0, 10.0, true, true, 0, 1, 2),
    ( 'Wedding Catering',FALSE, 'Full-service catering for weddings including appetizers, main courses, and desserts.', 3000.0, 15.0, true, true, 0, 2, 2),
    ( 'Live Band Performance',FALSE, 'Live music performance for weddings and concerts.', 2000.0, 5.0, true, true, 0, 2, 2);

-- Insert into services table
INSERT INTO service (id, specifics, duration, reservation_deadline, cancellation_deadline, reservation_type,minimum_arrangement,maximum_arrangement)
VALUES
    (1, 'Full event planning with vendor coordination', 2, 48, 24, 'AUTOMATIC',0,0),
    (2, 'Catering services for 100 guests', 2, 72, 48, 'AUTOMATIC',0,0),
    (3, 'Live band performance for 3 hours', 2, 24, 12, 'AUTOMATIC',0,0);

