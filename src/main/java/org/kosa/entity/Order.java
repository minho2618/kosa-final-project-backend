package org.kosa.entity;

public class Order {



    Table "orders" {
        "order_id" BIGINT [pk, increment]
        "user_id" BIGINT [not null]
        "status" orders_status_enum [not null, default: 'PENDING']
            "created_at" TIMESTAMP [not null, default: `CURRENT_TIMESTAMP`]
            "address" VARCHAR(300)
    }

    Table "order_items" {
        "order_item_id" BIGINT [pk, increment]
        "order_id" BIGINT [not null]
        "product_id" BIGINT [not null]
        "quantity" INT [not null]
        "unit_price" DECIMAL(12,2) [not null]
        "discount_value" DECIMAL(12,2)
        "total_price" DECIMAL(12,2) [not null]
    }
}
