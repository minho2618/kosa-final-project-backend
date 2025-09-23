package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.kosa.dto.order.OrderReq;
import org.kosa.dto.order.OrderRes;
import org.kosa.entity.Order;
import org.kosa.enums.OrderStatus;
import org.kosa.service.OrderService;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order", description = "주문 관리 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 생성 성공", content = @Content(schema = @Schema(implementation = OrderReq.class))),
            @ApiResponse(responseCode = "404", description = "주문 생성 실패", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/")
    public ResponseEntity<?> createOrder(@RequestBody OrderReq orderReq) {
        orderService.createOrder(orderReq);

        return ResponseEntity
                .status(201)
                .body(orderReq);
    }

    @Operation(summary = "주문 ID로 주문 조회", description = "주문 ID를 이용하여 특정 주문의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = OrderRes.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주문", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findOrderById(@Parameter(description = "조회할 주문의 ID", required = true) @PathVariable Long id) {
        Order order = orderService.findOrderById(id);
        OrderRes orderRes = OrderRes.toOrderRes(order);

        return ResponseEntity
                .status(200)
                .body(orderRes);
    }

    @Operation(summary = "회원의 전체 주문 조회", description = "특정 회원의 모든 주문 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/")
    public ResponseEntity<?> findOrdersByMember(@Parameter(description = "주문 목록을 조회할 회원의 ID", required = true) @RequestParam Long memberId) {
         List<Order> orderList = orderService.findOrdersByMember(memberId);

        return ResponseEntity
                .status(200)
                .body(orderList);
    }

    @Operation(summary = "회원의 연도별 주문 조회", description = "특정 회원의 특정 연도 주문 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/year")
    public ResponseEntity<?> findOrdersByYear(
            @Parameter(description = "조회할 회원의 ID", required = true) @RequestParam Long memberId,
            @Parameter(description = "조회할 연도", required = true, example = "2024") @RequestParam int year) {
        List<OrderRes> orderList = orderService.findOrdersByYear(memberId, year);

        return ResponseEntity
                .status(200)
                .body(orderList);
    }

    @Operation(summary = "주문 정보 수정", description = "기존 주문의 상태 및 주소 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주문", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(
            @Parameter(description = "수정할 주문의 ID", required = true) @PathVariable Long orderId,
            @RequestBody OrderStatus newStatus) {
        Order updatedOrder = orderService.updateOrder(orderId, newStatus);

        return ResponseEntity
                .status(201)
                .body(updatedOrder);
    }

    @Operation(summary = "주문 삭제", description = "주문을 시스템에서 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@Parameter(description = "삭제할 주문의 ID", required = true) @PathVariable Long orderId) {
        orderService.deleteOrder(orderId);

        return ResponseEntity
                .status(200)
                .body(orderId);
    }
}