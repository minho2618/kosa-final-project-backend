package org.kosa.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.kosa.dto.order.OrderReq;
import org.kosa.dto.order.OrderRes;
import org.kosa.entity.Order;
import org.kosa.service.OrderService;


import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "주문 관리 API")
public class OrderController {
    private final OrderService orderService;

    // 주문하기
    @PostMapping("/")
    public ResponseEntity<?> createOrder(@RequestBody OrderReq orderReq) {
        orderService.createOrder(orderReq);

        return ResponseEntity
                .status(201)
                .body(orderReq);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findOrderById(@PathVariable Long id) {
        Order order = orderService.findOrderById(id);
        OrderRes orderRes = OrderRes.toOrderRes(order);

        return ResponseEntity
                .status(200)
                .body(orderRes);
    }

    @GetMapping("/")
    public ResponseEntity<?> findOrdersByMember(@RequestParam Long memberId) {
         List<Order> orderList = orderService.findOrdersByMember(memberId);

        return ResponseEntity
                .status(200)
                .body(orderList);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable Long orderId, @RequestBody OrderReq orderReq) {
        orderService.updateOrder(orderId, orderReq);

        return ResponseEntity
                .status(201)
                .body(orderReq);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);

        return ResponseEntity
                .status(200)
                .body(orderId);
    }
}
