package org.kosa.entity;

import org.kosa.enums.UserRole;

import java.time.LocalDateTime;

public class User {

    private Long userId;

    private String username;

    private String email;

    private String password;

    private String phoneNum;

    private UserRole role;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

}
    /*
Table "sellers" {
  "user_id" BIGINT [pk, not null]
  "seller_name" VARCHAR(100) [not null]
  "seller_intro" VARCHAR(500)
  "seller_reg_no" VARCHAR(64) [not null]
  "seller_address" VARCHAR(255)
  "postal_code" VARCHAR(20)
  "country" VARCHAR(2) [default: 'KR']
  "created_at" TIMESTAMP [not null, default: `CURRENT_TIMESTAMP`]
  "updated_at" TIMESTAMP [not null, default: `CURRENT_TIMESTAMP`]

  Indexes {
    seller_reg_no [unique, name: "uk_sellers_reg"]
  }
}



Table "product_images" {
  "image_id" BIGINT [pk, increment]
  "product_id" BIGINT [not null]
  "url" VARCHAR(512) [not null]
  "alt_text" VARCHAR(200)
  "sort_order" INT [not null, default: 0]
  "created_at" TIMESTAMP [not null, default: `CURRENT_TIMESTAMP`]
}



Table "review_photos" {
  "photo_id" BIGINT [pk, increment]
  "review_id" BIGINT [not null]
  "url" VARCHAR(512) [not null]
  "sort_order" INT [not null, default: 0]
  "created_at" TIMESTAMP [not null, default: `CURRENT_TIMESTAMP`]
}

Table "product_questions" {
  "question_id" BIGINT [pk, increment]
  "product_id" BIGINT [not null]
  "user_id" BIGINT [not null]
  "category" VARCHAR(50)
  "content" TEXT [not null]
  "status" product_questions_status_enum [not null, default: 'OPEN']
  "created_at" TIMESTAMP [not null, default: `CURRENT_TIMESTAMP`]
  "updated_at" TIMESTAMP [not null, default: `CURRENT_TIMESTAMP`]

  Indexes {
    (product_id, status) [name: "idx_pq_product_status"]
  }
}

Table "product_question_answers" {
  "answer_id" BIGINT [pk, increment]
  "question_id" BIGINT [not null]
  "responder_id" BIGINT [not null]
  "content" TEXT [not null]
  "created_at" TIMESTAMP [not null, default: `CURRENT_TIMESTAMP`]
}

Table "product_question_photos" {
  "photo_id" BIGINT [pk, increment]
  "question_id" BIGINT [not null]
  "url" VARCHAR(512) [not null]
  "sort_order" INT [not null, default: 0]
  "created_at" TIMESTAMP [not null, default: `CURRENT_TIMESTAMP`]
}

Table "recipes" {
  "recipe_id" BIGINT [pk, increment]
  "user_id" BIGINT [not null]
  "title" VARCHAR(150) [not null]
  "description" TEXT
}
    */