package org.kosa.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;


    private Long userId;

    private String status;

    private LocalDateTime createdAt;

    private String address;

    private List<OrderItem> orderItemList;

}
