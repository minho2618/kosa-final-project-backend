package org.kosa.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.config.RabbitMQConfig;
import org.kosa.dto.event.InventoryItemEvent;
import org.kosa.dto.event.InventoryReservedEvent;
import org.kosa.dto.event.OrderCreatedEvent;
import org.kosa.dto.event.OrderItemEvent;
import org.kosa.dto.order.OrderReq;
import org.kosa.enums.OrderStatus;
import org.kosa.service.OrderService;
import org.kosa.service.ProductService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryListener {

    private final RabbitTemplate rabbitTemplate;
    private final OrderService orderService;
    private final ProductService productService;
    // private final Random random = new Random();

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("재고 서비스: 주문 생성 이벤트 수신 - {}, ", event);

        try {
            List<InventoryItemEvent> inventoryItems = new ArrayList<>();
            boolean allItemsAvailable = true;  // 모든 상품 재고 확보 가능 여부

            // 주문에 포함된 각 상품에 대해 재고 확인 및 예약
            for (OrderItemEvent orderItem : event.getOrderItems()) {
                // 재고 확인 및 예약 시도
                boolean isAvailable = productService.getProductDetail(orderItem.getProductId()).getIsActive();

                // 재고 처리 결과를 InventoryItemEvent로 변환
                InventoryItemEvent inventoryItem = InventoryItemEvent.builder()
                        .productId(orderItem.getProductId())
                        .requestedQuantity(orderItem.getQuantity())
                        .reservedQuantity(isAvailable ? orderItem.getQuantity() : 0)  // 성공시 요청수량, 실패시 0
                        .isAvailable(isAvailable)
                        .build();

                inventoryItems.add(inventoryItem);

                // 하나라도 재고 부족시 전체 실패 처리
                if (!isAvailable) {
                    allItemsAvailable = false;
                    log.warn("재고 부족: productId={}, 요청수량={}",
                            orderItem.getProductId(), orderItem.getQuantity());
                }
            }

            if (allItemsAvailable) {
                // ========== 모든 상품 재고 확보 성공 ==========
                log.info("재고 확보 성공: orderId={}", event.getOrderId());

                // 주문 상태를 '재고 확보됨'으로 변경
                orderService.updateOrder(event.getOrderId(), OrderStatus.READY);

                // 재고 확보 완료 이벤트 생성 및 발행
                InventoryReservedEvent inventoryEvent = InventoryReservedEvent.builder()
                        .orderId(event.getOrderId())
                        .inventoryItems(inventoryItems)
                        .timestamp(LocalDateTime.now())
                        .build();

                // 결제 서비스가 이 이벤트를 받아 결제 처리 시작
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.ORDER_EXCHANGE,
                        RabbitMQConfig.INVENTORY_RESERVED_ROUTING_KEY,
                        inventoryEvent
                );

                log.info("재고 확보 이벤트 발행: orderId={}", event.getOrderId());
            } else {
                // ========== 일부 또는 전체 상품 재고 부족 ==========
                // 이미 확보된 재고들을 모두 롤백
                rollbackReservedInventory(inventoryItems);

                // 주문을 취소 상태로 변경
                orderService.updateOrder(event.getOrderId(), OrderStatus.CANCELLED);

                log.warn("주문 취소: orderId={} - 재고 부족", event.getOrderId());
            }
         } catch (Exception e) {
            // 예외 발생시 주문을 실패 상태로 변경
            log.error("재고 처리 중 오류 발생: orderId={}", event.getOrderId(), e);
            orderService.updateOrder(event.getOrderId(), OrderStatus.FAILED);
            throw new RuntimeException("재고 처리 실패", e);
        }
    }

    /**
     * 이미 확보된 재고들을 롤백하는 메서드
     * 부분 실패 상황에서 이미 예약된 재고들을 원복시킴
     *
     * @param inventoryItems 재고 처리 결과 목록
     */
    private void rollbackReservedInventory(List<InventoryItemEvent> inventoryItems) {
        for (InventoryItemEvent item : inventoryItems) {
            // 실제로 확보된 수량이 있는 경우에만 롤백 수행
            if (productService.getProductDetail(item.getProductId()).getIsActive()) {
                log.info("재고 롤백: productId={}, quantity={}", item.getProductId(), item.getReservedQuantity());
            }
        }
    }

}
