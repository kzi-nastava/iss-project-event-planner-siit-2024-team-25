INSERT INTO users (id, first_name, last_name, user_role)
    VALUES (1, 'Organizator', 'Testic', 1);

INSERT INTO accounts (id, email, password, status, user_id)
    VALUES (1, 'e@e.e', '$2a$12$Dit.fzDZ1MJcWb6ykdmJsOOPY2hv651.YjgvoSYksNKBom15csnNW', 'ACTIVE', 1);

UPDATE users SET account_id = 1 WHERE id=1;

INSERT INTO event_organizers (id, phone_number, country, city, address, latitude, longitude)
    VALUES (1, '+38164646464', 'Serbia', 'Novi Sad', 'Despota Stefana 7', 45.2361912, 19.83889123661058);

INSERT INTO event_types (id,description, name, is_active)
    VALUES (1,'A meeting for consultation or discussion.', 'Conference', TRUE);

---password for accounts is 'password1'

INSERT INTO accounts ( id,email, password, status) VALUES
                                                    (2,'account1@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
                                                    (3,'account2@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
                                                    (4,'account3@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
                                                    (5,'account4@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
                                                    (6,'account5@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE');

INSERT INTO users (id, first_name, last_name, profile_picture_url, user_role, account_id)
VALUES
    ( 2,'John', 'Doe', 'https://example.com/johndoe.jpg', 1, 2),
    ( 3,'Jane', 'Smith', 'https://example.com/janesmith.jpg', 1, 3),
    ( 4,'Alice', 'Johnson', 'https://example.com/alicejohnson.jpg', 1, 4 ),
    ( 5,'Bob', 'Williams', 'https://example.com/bobwilliams.jpg', 1, 5),
    ( 6,'Charlie', 'Brown', 'https://example.com/charliebrown.jpg',1, 6);

UPDATE accounts SET user_id = 2 WHERE id=2;
UPDATE accounts SET user_id = 3 WHERE id=3;
UPDATE accounts SET user_id = 4 WHERE id=4;
UPDATE accounts SET user_id = 5 WHERE id=5;
UPDATE accounts SET user_id = 6 WHERE id=6;


INSERT INTO event_organizers (id, country, city, address, latitude, longitude, phone_number)
VALUES
    (2, 'Serbia', 'Beograd', 'Bulevar kralja Aleksandra 73', 44, 20, '+381601234567'),
    (3, 'Serbia', 'Novi Sad', 'Trg slobode 2', 45, 19, '+381641234567'),
    (4, 'Serbia', 'Niš', 'Njegoševa 12', 43, 21, '+381651234567'),
    (5, 'Serbia', 'Kragujevac', 'Knez Mihailova 22', 44, 20, '+381691234567'),
    (6, 'Serbia', 'Subotica', 'Zmaj Jovina 5', 46, 19, '+381112345678');

-- Insert event types
INSERT INTO event_types (id,description, name, is_active)
VALUES
    (2,'Music and Entertainment Event', 'Concert', true),
    (3,'Sporting Event', 'Football Match', true),
    (4,'Social Event', 'Wedding', false),
    (5,'Educational Event', 'Workshop', true);

-- Insert events
INSERT INTO events ( id,event_type_id, name, description, max_participants, privacy_type, start_date, end_date, start_time, end_time, country, city, address, latitude, longitude, organizer_id, created_date) VALUES
                                                                                                                                                                                                                ( 1,1, 'Tech Conference 2026', 'A conference about technology', 200, 0, '2026-12-16', '2026-12-16', '09:00', '14:00', 'Serbia', 'Belgrade', 'Bulevar Kralja Aleksandra 10', 44, 20, 1, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 2,2, 'Rock Concert', 'Live rock concert in Belgrade', 1000, 0, '2026-12-17', '2026-12-17', '20:00', '23:30', 'Serbia', 'Belgrade', 'Bulevar Kralja Aleksandra 10', 44, 20, 2, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 3,3, 'Football Match', 'Exciting football match between two rivals', 5000, 0, '2026-12-18', '2026-12-18', '17:00', '19:00', 'Serbia', 'Novi Sad', 'Trg Slobode 5', 45, 19, 1, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 4,4, 'Marko''s Wedding', 'Wedding celebration for Marko and Jelena', 150, 1, '2026-12-19', '2026-12-19', '18:00', '22:00', 'Serbia', 'Niš', 'Njegoševa 5', 43, 21, 2, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 5,5, 'Java Workshop', 'Workshop on advanced Java programming', 50, 0, '2026-12-20', '2026-12-20', '10:00', '15:00', 'Serbia', 'Belgrade', 'Bulevar Kralja Aleksandra 10', 44, 20, 1, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 6,1, 'AI Conference', 'A deep dive into artificial intelligence', 300, 0, '2026-12-21', '2026-12-21', '09:30', '14:30', 'Serbia', 'Novi Sad', 'Trg Slobode 5', 45, 19, 2, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 7,2, 'Jazz Concert', 'Smooth jazz evening', 500, 0, '2026-12-22', '2026-12-22', '20:00', '23:00', 'Serbia', 'Belgrade', 'Bulevar Kralja Aleksandra 10', 44, 20, 1, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 8,3, 'Local Football League', 'Local football match featuring top teams', 2000, 0, '2026-12-23', '2026-12-23', '18:30', '22:00', 'Serbia', 'Niš', 'Njegoševa 5', 43, 21, 2, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 9,4, 'Jelena''s Wedding', 'Jelena''s big day', 120, 1, '2026-12-24', '2026-12-24', '17:00', '21:30', 'Serbia', 'Novi Sad', 'Trg Slobode 5', 45, 19, 1, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 10,5, 'Design Thinking Workshop', 'Creative workshop for designers', 30, 0, '2026-12-25', '2026-12-25', '09:00', '12:30', 'Serbia', 'Belgrade', 'Bulevar Kralja Aleksandra 10', 44, 20, 2, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 11,1, 'Digital Marketing Conference', 'Digital marketing trends and techniques', 250, 0, '2026-12-16', '2026-12-16', '10:00', '14:30', 'Serbia', 'Novi Sad', 'Bulevar Oslobodjenja 50', 45, 19, 1, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 12,2, 'Classical Music Concert', 'An evening of classical music', 300, 0, '2026-12-18', '2026-12-18', '19:00', '22:30', 'Serbia', 'Belgrade', 'Kneza Mihaila 12', 44, 20, 2, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 13,3, 'Derby Football Match', 'Intense football derby between two big clubs', 5000, 0, '2026-12-20', '2026-12-20', '16:00', '20:00', 'Serbia', 'Niš', 'Njegoševa 12', 43, 21, 1, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 14,4, 'Svetlana''s Wedding', 'The wedding of Svetlana and Nikola', 200, 1, '2026-12-23', '2026-12-23', '17:00', '21:00', 'Serbia', 'Kragujevac', 'Karadjordjeva 5', 44, 20, 2, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 15,5, 'Leadership Skills Workshop', 'Workshop on leadership development', 60, 0, '2026-12-25', '2026-12-25', '09:00', '13:30', 'Serbia', 'Subotica', 'Trg Slobode 2', 46, 19, 1, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 16,1, 'Blockchain Conference', 'Blockchain technology and its future', 500, 0, '2026-12-28', '2026-12-28', '10:00', '14:30', 'Serbia', 'Belgrade', 'Nemanjina 4', 44, 20, 2, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 17,2, 'Pop Concert', 'Exciting pop concert with top performers', 2000, 0, '2026-12-30', '2026-12-30', '18:00', '22:00', 'Serbia', 'Novi Sad', 'Bulevar Oslobodjenja 50', 45, 19, 1, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 18,3, 'Charity Football Match', 'Charity event featuring famous football players', 1000, 0, '2025-01-05', '2025-01-05', '15:00', '19:30', 'Serbia', 'Niš', 'Njegoševa 12', 43, 21, 2, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 19,4, 'Sara''s Wedding', 'The beautiful wedding of Sara and Luka', 180, 1, '2025-01-10', '2025-01-10', '12:00', '16:00', 'Serbia', 'Belgrade', 'Kneza Mihaila 12', 44, 20, 1, CURRENT_TIMESTAMP),
                                                                                                                                                                                                                ( 20,5, 'Creative Writing Workshop', 'Workshop for aspiring writers', 40, 0, '2025-01-15', '2025-01-15', '09:00', '12:30', 'Serbia', 'Subotica', 'Trg Slobode 2', 46, 19, 2, CURRENT_TIMESTAMP);


-- Insert Accounts
INSERT INTO accounts ( id,email, password, status)
VALUES
    (7,'marko.p@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    (8,'ana.j@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    (9,'nikola.i@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    (10,'mila.v@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    (11,'petar.s@example.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    (12,'peric@petar.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE'),
    (13,'mika@mikic.com', '$2a$12$yto6FS5Fzul7kr1zPMxUZuzlzYazUy.VyAArUqZx8WasxhW3sn5km', 'ACTIVE');


-- Insert Users
INSERT INTO users (id, first_name, last_name, profile_picture_url, user_role, account_id)
VALUES
    (7,'Marko', 'Petrović', 'https://example.com/marko.jpg', 2, 7),
    (8,'Ana', 'Jovanović', 'https://example.com/ana.jpg', 2, 8),
    ( 9,'Nikola', 'Ilić', 'https://example.com/nikola.jpg', 2, 9),
    ( 10,'Mila', 'Vuković', 'https://example.com/mila.jpg', 2, 10),
    ( 11,'Petar', 'Stojanović', 'https://example.com/petar.jpg', 2, 11),
    ( 12,'Petar', 'Peric', 'https://example.com/peric.jpg', 0, 12),
    ( 13,'Mika', 'Mikic', 'https://example.com/mika.jpg', 3, 13);

UPDATE accounts SET user_id = 7 WHERE id=7;
UPDATE accounts SET user_id = 8 WHERE id=8;
UPDATE accounts SET user_id = 9 WHERE id=9;
UPDATE accounts SET user_id = 10 WHERE id=10;
UPDATE accounts SET user_id = 11 WHERE id=11;
UPDATE accounts SET user_id = 12 WHERE id=12;
UPDATE accounts SET user_id = 13 WHERE id=13;

-- Insert Owners (References Users)
INSERT INTO owners (id, company_name, country, city, address, latitude, longitude, contact_phone, description)
VALUES
    (7, 'Petrović Catering', 'Srbija', 'Beograd', 'Knez Mihailova 12', 44, 20, '+381641114567', 'Najbolja ketering usluga u Beogradu.'),
    (8, 'Jovanović Event Studio', 'Srbija', 'Novi Sad', 'Zmaj Jovina 10', 45, 19, '+381631114500', 'Planiranje događaja i dekoracija prostora.'),
    (9, 'Ilić Event Solutions', 'Srbija', 'Niš', 'Obrenovićeva 5', 43, 21, '+381610201567', 'Kompletne usluge za organizaciju venčanja.'),
    (10, 'Vuković Dekoracije', 'Srbija', 'Kragujevac', 'Glavna 15', 44, 20, '+38162114567', 'Unikatne dekoracije za sve vrste događaja.'),
    (11, 'Stojanović Catering', 'Srbija', 'Subotica', 'Trg Slobode 1', 46, 19, '+381620214567', 'Specijaliteti domaće kuhinje i ketering.');

-- Insert Offering Categories
INSERT INTO offering_type (id, name, description, status)
VALUES
    (1,'Catering Services', 'Catering services for various event types such as weddings, corporate events, and more.', 'ACCEPTED'),
    ( 2,'Entertainment Services', 'Entertainment services including live performers, musicians, and DJs.', 'ACCEPTED'),
    (3,'Technical Services', 'Technical services including sound systems, lighting, and AV equipment for events.', 'ACCEPTED'),
    ( 4,'Event Planning Services', 'Full event planning services, including coordination, decoration, and security.', 'ACCEPTED'),
    ( 5,'Event Equipment', 'Products for event setups such as tents, furniture, and staging.', 'ACCEPTED'),
    ( 6,'Event Decorations', 'Decorative products like flowers, banners, and table centerpieces.', 'ACCEPTED'),
    ( 7,'Gifts and Favors', 'Products like wedding favors, corporate gifts, and promotional materials for events.', 'ACCEPTED'),
    ( 8,'Event Apparel', 'Event-related apparel such as branded t-shirts, uniforms, and clothing for guests and staff.', 'ACCEPTED');

INSERT INTO  event_types_offering_categories (event_type_id, offering_categories_id)
VALUES (2, 1),
       (2, 2),
       (2,3),
       (2,4);

INSERT INTO offerings (id, name,deleted, description, price, discount, is_visible, is_available, status, offering_category_id, owner_id)
VALUES
    ( 1,'Portable Stage',FALSE, 'A portable stage suitable for concerts, conferences, and workshops.', 1500.0, 10.0, true, true, 0, 1, 7),
    ( 2,'Wedding Table Centerpieces',FALSE, 'Elegant floral arrangements for weddings.', 200.0, 15.0, true, true, 0, 2, 7),
    ( 3,'Football Jersey',FALSE, 'Official team jersey for football matches.', 75.0, 0.0, true, true, 0, 3, 7),
    ( 4,'Personalized Wedding Favors',FALSE, 'Custom wedding favors for guests.', 5.0, 0.0, true, true, 0, 4, 8),
    ( 5,'Portable Stage 2',FALSE, 'A portable stage suitable for concerts, conferences, and workshops.', 1500.0, 10.0, true, true, 0, 1,8),
    ( 6,'Wedding Table Centerpieces 2',FALSE, 'Elegant floral arrangements for weddings.', 200.0, 15.0, true, true, 0, 2, 9),
    ( 7,'Football Jersey 2',FALSE, 'Official team jersey for football matches.', 75.0, 0.0, true, true,0, 3, 10),
    ( 8,'Personalized Wedding Favors 2',FALSE, 'Custom wedding favors for guests.', 5.0, 0.0, true, true, 0, 4, 11);

INSERT INTO product values
                        (1),
                        (2),
                        (3),
                        (4), (5), (6), (7), (8);

INSERT INTO offerings (id, name,deleted, description, price, discount, is_visible, is_available, status, offering_category_id, owner_id)
VALUES
    ( 9,'Corporate Event Planning',FALSE, 'End-to-end planning for corporate events including venue selection, scheduling, and coordination.', 5000.0, 10.0, true, true, 0, 1, 7),
    ( 10,'Wedding Catering',FALSE, 'Full-service catering for weddings including appetizers, main courses, and desserts.', 3000.0, 15.0, true, true, 0, 2, 8),
    ( 11,'Live Band Performance',FALSE, 'Live music performance for weddings and concerts.', 2000.0, 5.0, true, true, 0, 3, 8);

-- Insert into services table
INSERT INTO service (id, specifics, duration, reservation_deadline, cancellation_deadline, reservation_type,minimum_arrangement,maximum_arrangement)
VALUES
    (9, 'Full event planning with vendor coordination', 72, 48, 24, 'AUTOMATIC',0,350),
    (10, 'Catering services for 100 guests', 12, 72, 48, 'AUTOMATIC',0,350),
    (11, 'Live band performance for 3 hours', 3, 24, 12, 'AUTOMATIC',0,350);

-- Insert Purchases
INSERT INTO purchases (id, amount, start_date, start_time, end_date, end_time, event_id, offering_id)
VALUES
    ( 1,2000.0, CURRENT_DATE, CURRENT_TIME, CURRENT_DATE, CURRENT_TIME, 1, 1),
    ( 2,2000.0, CURRENT_DATE, CURRENT_TIME, CURRENT_DATE, CURRENT_TIME, 2, 2),
    ( 3,2000.0, CURRENT_DATE, CURRENT_TIME, CURRENT_DATE, CURRENT_TIME, 3, 3),
    ( 4,2000.0, CURRENT_DATE, CURRENT_TIME, CURRENT_DATE, CURRENT_TIME, 4, 4),
    ( 5,2000.0, CURRENT_DATE, CURRENT_TIME, CURRENT_DATE, CURRENT_TIME, 5, 5);

-- Insert OfferingReviews
INSERT INTO review ( comment, created_date, rating, review_status, review_type, purchase_id, user_id)
VALUES
    ( 'Comment1',CURRENT_TIMESTAMP, 5, 'APPROVED','OFFERING_REVIEW', 1, 6),
    ( 'Comment2',CURRENT_TIMESTAMP, 5, 'APPROVED','OFFERING_REVIEW', 2,1),
    ( 'Comment3',CURRENT_TIMESTAMP, 5, 'APPROVED','OFFERING_REVIEW', 3,1),
    ('Comment4',CURRENT_TIMESTAMP, 5, 'APPROVED','OFFERING_REVIEW', 4,1),
    ( 'Comment5',CURRENT_TIMESTAMP, 5, 'APPROVED','OFFERING_REVIEW', 5,1);


