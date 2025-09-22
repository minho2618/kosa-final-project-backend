package org.kosa.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.config.RabbitMQConfig;
import org.kosa.entity.Order;
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
    // private final Random random = new Random();

    @RabbitListener(queues = RabbitMQConfig.INVENTORY_QUEUE)
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("결제 서비스: 재고 확보 이벤트 수신 - {}", event);

        try {
            // 결제 처리 시뮬레이션 (3초 소요)
            Thread.sleep(3000);

            // 95% 확률로 결제 성공
            if (random.nextDouble() < 0.95) {
                // 결제 성공
                String paymentId = "PAY_" + UUID.randomUUID().toString().substring(0, 8);
                BigDecimal amount = BigDecimal.valueOf(50000); // 임시 금액

                log.info("결제 성공: 주문ID={}, 결제ID={}, 금액={}",
                        event.getOrderId(), paymentId, amount);

                // 주문 상태 업데이트
                orderService.updateOrderStatus(event.getOrderId(),
                        Order.OrderStatus.PAYMENT_PROCESSED);

                // 결제 완료 이벤트 발행
                PaymentProcessedEvent paymentEvent = PaymentProcessedEvent.builder()
                        .orderId(event.getOrderId())
                        .amount(amount)
                        .paymentId(paymentId)
                        .timestamp(LocalDateTime.now())
                        .build();

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.ORDER_EXCHANGE,
                        RabbitMQConfig.PAYMENT_PROCESSED_ROUTING_KEY,
                        paymentEvent
                );

                log.info("결제 완료 이벤트 발행: {}", paymentEvent);

            } else {
                // 결제 실패
                log.warn("결제 실패: 주문ID={}", event.getOrderId());
                orderService.updateOrderStatus(event.getOrderId(),
                        Order.OrderStatus.FAILED);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("결제 처리 중 오류 발생", e);
            throw new RuntimeException("결제 처리 실패", e);
        }
    }
}
