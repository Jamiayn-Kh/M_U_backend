CREATE TABLE IF NOT EXISTS mold_orders (
    id BIGSERIAL PRIMARY KEY,

    seller_id BIGINT NOT NULL,
    city_handler_id BIGINT,

    status VARCHAR(30) NOT NULL,
    note VARCHAR(500),

    departure_date DATE,
    departure_time TIME,
    bus_number VARCHAR(50),
    driver_phone VARCHAR(20),
    transport_note VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP,
    received_at TIMESTAMP,
    transported_at TIMESTAMP,
    completed_at TIMESTAMP,

    CONSTRAINT fk_mold_orders_seller
        FOREIGN KEY (seller_id)
        REFERENCES users(id),

    CONSTRAINT fk_mold_orders_city_handler
        FOREIGN KEY (city_handler_id)
        REFERENCES users(id),

    CONSTRAINT chk_mold_orders_status
        CHECK (
            status IN (
                'DRAFT',
                'SENT',
                'RECEIVED',
                'IN_PROCESS',
                'TRANSPORTED',
                'COMPLETED',
                'CANCELLED'
            )
        )
);

CREATE TABLE IF NOT EXISTS mold_order_items (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL,
    mold_code VARCHAR(5) NOT NULL,
    code_prefix VARCHAR(1) NOT NULL,
    stone_required BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_mold_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES mold_orders(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_mold_order_items_code
        CHECK (mold_code ~ '^[AKS][0-9]{1,4}$'),

    CONSTRAINT chk_mold_order_items_prefix
        CHECK (code_prefix IN ('A', 'K', 'S')),

    CONSTRAINT uk_mold_order_items_order_code
        UNIQUE (order_id, mold_code)
);

CREATE INDEX IF NOT EXISTS idx_mold_orders_seller_id
    ON mold_orders(seller_id);

CREATE INDEX IF NOT EXISTS idx_mold_orders_city_handler_id
    ON mold_orders(city_handler_id);

CREATE INDEX IF NOT EXISTS idx_mold_orders_status
    ON mold_orders(status);

CREATE INDEX IF NOT EXISTS idx_mold_order_items_order_id
    ON mold_order_items(order_id);

CREATE INDEX IF NOT EXISTS idx_mold_order_items_prefix
    ON mold_order_items(code_prefix);