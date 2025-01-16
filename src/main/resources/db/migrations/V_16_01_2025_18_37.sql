CREATE TABLE administrators_notifications
(
    administrator_id BIGINT NOT NULL,
    notifications_id BIGINT NOT NULL
);

CREATE TABLE event_organizers_notifications
(
    event_organizer_id BIGINT NOT NULL,
    notifications_id   BIGINT NOT NULL
);

CREATE TABLE owners_notifications
(
    owner_id         BIGINT NOT NULL,
    notifications_id BIGINT NOT NULL
);

CREATE TABLE product_event_types
(
    product_id     BIGINT NOT NULL,
    event_types_id BIGINT NOT NULL
);

CREATE UNIQUE INDEX IX_pk_eventattendance ON event_attendance (attendee_id, event_id);

ALTER TABLE administrators_notifications
    ADD CONSTRAINT fk_admnot_on_administrator FOREIGN KEY (administrator_id) REFERENCES administrators (id);

ALTER TABLE administrators_notifications
    ADD CONSTRAINT fk_admnot_on_notification FOREIGN KEY (notifications_id) REFERENCES notification (id);

ALTER TABLE event_organizers_notifications
    ADD CONSTRAINT fk_eveorgnot_on_event_organizer FOREIGN KEY (event_organizer_id) REFERENCES event_organizers (id);

ALTER TABLE event_organizers_notifications
    ADD CONSTRAINT fk_eveorgnot_on_notification FOREIGN KEY (notifications_id) REFERENCES notification (id);

ALTER TABLE owners_notifications
    ADD CONSTRAINT fk_ownnot_on_notification FOREIGN KEY (notifications_id) REFERENCES notification (id);

ALTER TABLE owners_notifications
    ADD CONSTRAINT fk_ownnot_on_owner FOREIGN KEY (owner_id) REFERENCES owners (id);

ALTER TABLE product_event_types
    ADD CONSTRAINT fk_proevetyp_on_event_type FOREIGN KEY (event_types_id) REFERENCES event_types (id);

ALTER TABLE product_event_types
    ADD CONSTRAINT fk_proevetyp_on_product FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE budget_item
    ALTER COLUMN amount SET NOT NULL;

ALTER TABLE purchases
    ALTER COLUMN amount SET NOT NULL;

ALTER TABLE event_invitations
    ALTER COLUMN guest_email DROP NOT NULL;

ALTER TABLE review
    ALTER COLUMN purchase_id SET NOT NULL;