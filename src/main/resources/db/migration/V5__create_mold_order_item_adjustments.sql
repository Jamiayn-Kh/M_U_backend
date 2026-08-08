CREATE TABLE mold_order_item_adjustments (
    id BIGSERIAL PRIMARY KEY,

    order_item_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL,

    final_mold_code VARCHAR(5),
    final_quantity INTEGER NOT NULL,

    note VARCHAR(500),

    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    approved BOOLEAN NOT NULL DEFAULT FALSE,
    approved_at TIMESTAMP,

    CONSTRAINT fk_adjustments_order_item
        FOREIGN KEY (order_item_id)
        REFERENCES mold_order_items(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_adjustments_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT chk_adjustments_action
        CHECK (action IN ('KEEP', 'ADD', 'CANCEL')),

    CONSTRAINT chk_adjustments_quantity
        CHECK (final_quantity >= 0),

    CONSTRAINT chk_adjustments_mold_code
        CHECK (
            final_mold_code IS NULL
            OR final_mold_code ~ '^[AKS][0-9]{1,4}$'
        )
);

CREATE INDEX idx_adjustments_order_item_id
    ON mold_order_item_adjustments(order_item_id);

CREATE INDEX idx_adjustments_created_by
    ON mold_order_item_adjustments(created_by);