package org.kosa.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.config.RabbitMQConfig;
import org.kosa.dto.event.InventoryReservedEvent;
import org.kosa.dto.event.PaymentProcessedEvent;
import org.kosa.entity.Order;
import org.kosa.enums.OrderStatus;
import org.kosa.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentListener {

    private final RabbitTemplate rabbitTemplate;
    private final OrderService orderService;
    // private final PaymentService paymentService;

    /**
     * 재고 확보 완료 이벤트 처리
     * 재고가 확보된 주문에 대해 결제 처리를 수행
     *
     * @param event 재고 확보 완료 이벤트 정보
     */
    @RabbitListener(queues = RabbitMQConfig.INVENTORY_QUEUE)
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("결제 서비스: 재고 확보 이벤트 수신 - {}", event);

        try {
            // 1. 주문 정보 조회 (결제 금액 및 회원 정보 확인용)
            Order order = orderService.findOrderById(event.getOrderId());

            // 2. 주문 총 금액 계산
            // OrderItem들의 totalPrice를 합산하여 최종 결제 금액 산출
            BigDecimal totalAmount = order.getOrderItemList().stream()
                    .map(orderItem -> orderItem.getTotalPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 3. 실제 결제 처리
            // 외부 PG사 API 호출 또는 내부 결제 시스템 연동
            /*String paymentId = paymentService.processPayment(
                    order.getMember().getMemberId(),  // 결제자 회원 ID
                    totalAmount,                      // 결제 금액
                    "CARD"                           // 결제 수단 (실제로는 주문시 선택된 수단 사용)
            );*/
            String paymentId = "CARD";

            if (paymentId != null) {
                // ========== 결제 성공 ==========
                log.info("결제 성공: orderId={}, paymentId={}, amount={}", event.getOrderId(), paymentId, totalAmount);
                
                // 주문 상태를 '결제 완료'로 변경
                orderService.updateOrder(event.getOrderId(), OrderStatus.READY);

                // 결제 완료 이벤트 생성 및 발행
                PaymentProcessedEvent paymentEvent = PaymentProcessedEvent.builder()
                        .orderId(event.getOrderId())
                        .memberId(order.getMember().getMemberId())
                        .totalAmount(totalAmount)
                        .paymentId(paymentId)
                        .paymentMethod("CARD")
                        .timestamp(LocalDateTime.now())
                        .build();

                // 배송 서비스가 이 이벤트를 받아 배송 처리 시작
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.ORDER_EXCHANGE,
                        RabbitMQConfig.PAYMENT_PROCESSED_ROUTING_KEY,
                        paymentEvent
                );

                log.info("결제 완료 이벤트 발행: orderId={}", event.getOrderId());

            } else {
                // ========== 결제 실패 ==========
                log.warn("결제 실패: orderId={}", event.getOrderId());

                // 주문 상태를 실패로 변경
                orderService.updateOrder(event.getOrderId(), OrderStatus.FAILED);

                // 이미 확보된 재고를 롤백해야 함
                rollbackInventory(event);
            }

        } catch (Exception e) {
            Thread.currentThread().interrupt();
            log.error("결제 처리 중 오류 발생", e);
            throw new RuntimeException("결제 처리 실패", e);
        }
    }

    /**
     * 결제 실패시 확보된 재고를 롤백
     * 실제 구현에서는 재고 서비스에 롤백 이벤트를 발행하여 처리
     *
     * @param event 재고 확보 이벤트 (롤백할 재고 정보 포함)
     */
    private void rollbackInventory(InventoryReservedEvent event) {
        log.info("재고 롤백 요청: orderId={}", event.getOrderId());

        // TODO: 실제로는 재고 롤백 전용 이벤트를 발행하여 처리
        // InventoryRollbackEvent를 생성하여 재고 서비스로 전송
        // 현재는 로그만 남기고 실제 롤백은 재고 서비스에서 별도 구현 필요

        // 예시: 재고 롤백 이벤트 발행
        // InventoryRollbackEvent rollbackEvent = InventoryRollbackEvent.builder()
        //     .orderId(event.getOrderId())
        //     .inventoryItems(event.getInventoryItems())
        //     .build();
        // rabbitTemplate.convertAndSend(EXCHANGE, "inventory.rollback", rollbackEvent);
    }
}
