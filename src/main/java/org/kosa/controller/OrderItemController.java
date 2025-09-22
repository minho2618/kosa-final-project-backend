package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.orderItem.OrderItemReq;
import org.kosa.dto.orderItem.OrderItemRes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "OrderItem", description = "주문 항목 관리 API")
@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final org.kosa.service.OrderItemService orderItemService;

    @Operation(summary = "주문 항목 생성", description = "주문에 포함될 개별 항목을 생성합니다.")
    @ApiResponse(responseCode = "200", description = "생성 성공, 생성된 주문 항목 ID 반환")
    @PostMapping("")
    public ResponseEntity<?> createOrderItem(@RequestBody OrderItemReq orderItemReq) {
        Long orderItemId = orderItemService.createOrderItem(orderItemReq);

        return ResponseEntity
                .status(200)
                .body(orderItemId);
    }

    @Operation(summary = "특정 주문의 모든 항목 조회", description = "주문 ID를 사용하여 해당 주문에 속한 모든 항목을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/order")
    public ResponseEntity<?> findOrderItemsByOrder(@Parameter(description = "항목을 조회할 주문의 ID", required = true) @RequestParam Long orderId) {
        List<OrderItemRes> orderItemResList = orderItemService.findOrderItemsByOrder(orderId);

        return ResponseEntity
                .status(200)
                .body(orderItemResList);
    }

    /*@Operation(summary = "특정 상품이 포함된 모든 주문 항목 조회", description = "상품 ID를 사용하여 해당 상품이 포함된 모든 주문 항목을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/product")
    public ResponseEntity<?> findOrderItemsByProduct(@Parameter(description = "조회할 상품의 ID", required = true) @RequestParam Long productId) {
        List<OrderItemRes> orderItemResList = orderItemService.findOrderItemsByProduct(productId);

        return ResponseEntity
                .status(200)
                .body(orderItemResList);
    }*/

    @Operation(summary = "주문 항목 수정", description = "기존 주문 항목의 정보를 수정합니다.")
    @ApiResponse(responseCode = "201", description = "수정 성공")
    @PutMapping("/{orderItemId}")
    public ResponseEntity<?> updateOrderItem(
            @Parameter(description = "수정할 주문 항목의 ID", required = true) @PathVariable Long orderItemId,
            @RequestBody OrderItemReq orderItemReq) {
        orderItemService.updateOrderItem(orderItemId, orderItemReq);

        return ResponseEntity
                .status(201)
                .body(orderItemReq);
    }

    @Operation(summary = "주문 항목 삭제", description = "주문 항목을 시스템에서 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공, 삭제된 주문 항목 ID 반환")
    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<?> deleteOrderItem(@Parameter(description = "삭제할 주문 항목의 ID", required = true) @PathVariable Long orderItemId) {
        orderItemService.deleteOrderItem(orderItemId);

        return ResponseEntity
                .status(200)
                .body(orderItemId);
    }
}