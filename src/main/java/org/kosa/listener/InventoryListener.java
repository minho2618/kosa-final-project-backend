package org.kosa.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.config.RabbitMQConfig;
import org.kosa.dto.order.OrderReq;
import org.kosa.service.OrderService;
import org.kosa.service.ProductService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    public void handleOrderCreated(OrderReq req) {
        log.info("재고 서비스: 주문 생성 이벤트 수신 - {}, ", req);

        try {
            // 재고 확인 시뮬레이션 (2초 소요)
            Thread.sleep(2000);

            // 90% 확률로 재고 확보 성공
            if (random.nextDouble() < 0.9) {
                // 재고 확보 성공
                log.info("재고 확보 성공: 주문ID={}, 상품ID={}, 수량={}",
                        event.getOrderId(), event.getProductId(), event.getQuantity());

                // 주문 상태 업데이트
                orderService.updateOrderStatus(event.getOrderId(),
                        Order.OrderStatus.INVENTORY_RESERVED);

                // 재고 확보 이벤트 발행
                InventoryReservedEvent inventoryEvent = InventoryReservedEvent.builder()
                        .orderId(event.getOrderId())
                        .productId(event.getProductId())
                        .quantity(event.getQuantity())
                        .timestamp(LocalDateTime.now())
                        .build();

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.ORDER_EXCHANGE,
                        RabbitMQConfig.INVENTORY_RESERVED_ROUTING_KEY,
                        inventoryEvent
                );

                log.info("재고 확보 이벤트 발행: {}", inventoryEvent);
            } else {
                // 재고 부족
                log.warn("재고 부족: 주문ID={}, 상품ID={}", event.getOrderId(), event.getProductId());
                orderService.updateOrderStatus(event.getOrderId(),
                        Order.OrderStatus.CANCELLED);
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("재고 처리 중 오류 발생", e);
            throw new RuntimeException("재고 처리 실패", e);
        }
    }

}
