ALTER TABLE budget_item
    ADD offering_category_id BIGINT;

ALTER TABLE budget_item
    ADD CONSTRAINT FK_BUDGETITEM_ON_OFFERINGCATEGORY FOREIGN KEY (offering_category_id) REFERENCES offering_type (id);

ALTER TABLE budget_item
DROP
COLUMN offering_category_type;

ALTER TABLE budget_item
    ALTER COLUMN amount SET NOT NULL;