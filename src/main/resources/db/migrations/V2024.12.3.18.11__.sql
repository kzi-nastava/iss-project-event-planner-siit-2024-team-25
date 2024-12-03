ALTER TABLE administrators_notifications
    DROP CONSTRAINT fk_admnot_on_administrator;

ALTER TABLE administrators_notifications
    DROP CONSTRAINT fk_admnot_on_notification;

ALTER TABLE event_organizers_notifications
    DROP CONSTRAINT fk_eveorgnot_on_event_organizer;

ALTER TABLE event_organizers_notifications
    DROP CONSTRAINT fk_eveorgnot_on_notification;

ALTER TABLE event_types_offering_categories
    DROP CONSTRAINT fk_evetypoffcat_on_event_type;

ALTER TABLE event_types_offering_categories
    DROP CONSTRAINT fk_evetypoffcat_on_offering_category;

ALTER TABLE offering_images
    DROP CONSTRAINT fk_offering_images_on_offering;

ALTER TABLE offerings_event_types
    DROP CONSTRAINT fk_offevetyp_on_event_type;

ALTER TABLE offerings_event_types
    DROP CONSTRAINT fk_offevetyp_on_offering;

ALTER TABLE owner_company_pictures
    DROP CONSTRAINT fk_owner_companypictures_on_owner;

ALTER TABLE owners_notifications
    DROP CONSTRAINT fk_ownnot_on_notification;

ALTER TABLE owners_notifications
    DROP CONSTRAINT fk_ownnot_on_owner;

ALTER TABLE product_event_types
    DROP CONSTRAINT fk_proevetyp_on_event_type;

ALTER TABLE product_event_types
    DROP CONSTRAINT fk_proevetyp_on_product;

ALTER TABLE register_requests
    DROP CONSTRAINT fk_register_requests_on_account;

ALTER TABLE service_event_types
    DROP CONSTRAINT fk_serevetyp_on_event_type;

ALTER TABLE service_event_types
    DROP CONSTRAINT fk_serevetyp_on_service;

ALTER TABLE register_requests
    ADD email VARCHAR(255);

ALTER TABLE register_requests
    ADD password VARCHAR(255);

ALTER TABLE register_requests
    ADD user_id BIGINT;

ALTER TABLE register_requests
    ALTER COLUMN email SET NOT NULL;

ALTER TABLE register_requests
    ALTER COLUMN password SET NOT NULL;

ALTER TABLE accounts
    ADD CONSTRAINT uc_accounts_email UNIQUE (email);

ALTER TABLE register_requests
    ADD CONSTRAINT uc_register_requests_verificationcode UNIQUE (verification_code);

ALTER TABLE register_requests
    ADD CONSTRAINT FK_REGISTER_REQUESTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

DROP TABLE administrators_notifications CASCADE;

DROP TABLE event_organizers_notifications CASCADE;

DROP TABLE event_types_offering_categories CASCADE;

DROP TABLE offering_images CASCADE;

DROP TABLE offerings_event_types CASCADE;

DROP TABLE owner_company_pictures CASCADE;

DROP TABLE owners_notifications CASCADE;

DROP TABLE product_event_types CASCADE;

DROP TABLE service_event_types CASCADE;

ALTER TABLE register_requests
    DROP COLUMN account_id;

ALTER TABLE users
    ALTER COLUMN account_id DROP NOT NULL;

ALTER TABLE accounts
    DROP COLUMN status;

ALTER TABLE accounts
    ADD status VARCHAR(255) NOT NULL;