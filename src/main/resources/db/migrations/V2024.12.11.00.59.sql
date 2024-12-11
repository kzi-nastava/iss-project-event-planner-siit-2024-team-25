ALTER TABLE service
    ADD max_arrangement INTEGER;

ALTER TABLE service
    ADD min_arrangement INTEGER;
ALTER TABLE offerings
    ADD deleted BOOLEAN;