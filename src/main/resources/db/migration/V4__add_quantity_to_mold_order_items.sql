ALTER TABLE mold_order_items
ADD COLUMN quantity INTEGER NOT NULL DEFAULT 1;

ALTER TABLE mold_order_items
ADD CONSTRAINT chk_mold_order_items_quantity
CHECK (quantity > 0);