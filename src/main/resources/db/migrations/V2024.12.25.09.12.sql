ALTER TABLE events
    DROP COLUMN latitude;

ALTER TABLE events
    DROP COLUMN longitude;

ALTER TABLE events
    ADD latitude DOUBLE PRECISION;

ALTER TABLE events
    ADD longitude DOUBLE PRECISION;

ALTER TABLE event_organizers
    DROP COLUMN latitude;

ALTER TABLE event_organizers
    DROP COLUMN longitude;

ALTER TABLE event_organizers
    ADD latitude DOUBLE PRECISION;

ALTER TABLE event_organizers
    ADD longitude DOUBLE PRECISION;

ALTER TABLE owners
    DROP COLUMN latitude;

ALTER TABLE owners
    DROP COLUMN longitude;

ALTER TABLE owners
    ADD latitude DOUBLE PRECISION;

ALTER TABLE owners
    ADD longitude DOUBLE PRECISION;