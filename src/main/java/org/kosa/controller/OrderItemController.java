package org.kosa.controller;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.orderItem.OrderItemReq;
import org.kosa.dto.orderItem.OrderItemRes;
import org.kosa.entity.Product;
import org.kosa.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping("")
    public ResponseEntity<?> createOrderItem(OrderItemReq orderItemReq) {
        Long orderItemId = orderItemService.createOrderItem(orderItemReq);

        return ResponseEntity
                .status(200)
                .body(orderItemId);
    }

    @GetMapping("")
    public ResponseEntity<?> findOrderItemsByOrder(@RequestParam Long orderId) {
        List<OrderItemRes> orderItemResList = orderItemService.findOrderItemsByOrder(orderId);

        return ResponseEntity
                .status(200)
                .body(orderItemResList);
    }

    @GetMapping("")
    public ResponseEntity<?> findOrderItemsByProduct(@RequestParam Long productId) {
        List<OrderItemRes> orderItemResList = orderItemService.findOrderItemsByProduct(productId);

        return ResponseEntity
                .status(200)
                .body(orderItemResList);
    }

    /*@Transactional(readOnly = true)
    public ResponseEntity<?> findByQuantityGreaterThan(int quantity) {
        return orderItemRepository.findByQuantityGreaterThan(quantity);

        return ResponseEntity
                .status(200)
                .body(orderItemId);
    }*/

    /*@Transactional(readOnly = true)
    public ResponseEntity<?> findByIdWithProduct(Long id) {
        return orderItemRepository.findByIdWithProduct(id);

        return ResponseEntity
                .status(200)
                .body(orderItemId);
    }*/

    /*@Transactional(readOnly = true)
    public ResponseEntity<?> findByTotalPriceGreaterThanEqual(double price) {
        return orderItemRepository.findByTotalPriceGreaterThanEqual(price);

        return ResponseEntity
                .status(200)
                .body(orderItemId);
    }*/

    @PutMapping("/{orderItemId}")
    public ResponseEntity<?> updateOrderItem(@PathVariable Long orderItemId, @RequestBody OrderItemReq orderItemReq) {
        orderItemService.updateOrderItem(orderItemId, orderItemReq);

        return ResponseEntity
                .status(201)
                .body(orderItemReq);
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<?> deleteOrderItem(@PathVariable Long orderItemId) {
        orderItemService.deleteOrderItem(orderItemId);

        return ResponseEntity
                .status(200)
                .body(orderItemId);
    }
}
