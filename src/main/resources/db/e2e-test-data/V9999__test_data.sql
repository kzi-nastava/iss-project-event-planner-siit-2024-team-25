INSERT INTO users (id, first_name, last_name, user_role)
    VALUES (1, 'Organizator', 'Testic', 1);

INSERT INTO accounts (id, email, password, status, user_id)
    VALUES (1, 'e@e.e', '$2a$12$Dit.fzDZ1MJcWb6ykdmJsOOPY2hv651.YjgvoSYksNKBom15csnNW', 'ACTIVE', 1);

UPDATE users SET account_id = 1 WHERE id=1;