INSERT INTO users (id, first_name, last_name, user_role)
    VALUES (1, 'Organizator', 'Testic', 1);

INSERT INTO accounts (id, email, password, status, user_id)
    VALUES (1, 'e@e.e', '$2a$12$Dit.fzDZ1MJcWb6ykdmJsOOPY2hv651.YjgvoSYksNKBom15csnNW', 'ACTIVE', 1);

UPDATE users SET account_id = 1 WHERE id=1;

INSERT INTO event_organizers (id, phone_number, country, city, address, latitude, longitude)
    VALUES (1, '+38164646464', 'Serbia', 'Novi Sad', 'Despota Stefana 7', 45.2361912, 19.83889123661058);

INSERT INTO event_types (description, name, is_active)
    VALUES ('A meeting for consultation or discussion.', 'Conference', TRUE);