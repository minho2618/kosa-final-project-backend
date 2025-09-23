package org.kosa.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.config.RabbitMQConfig;
import org.kosa.dto.event.PaymentProcessedEvent;
import org.kosa.dto.event.ShippingStartedEvent;
import org.kosa.enums.OrderStatus;
import org.kosa.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShippingListener {

    private final RabbitTemplate rabbitTemplate;
    private final OrderService orderService;
    // private final ShippingService shippingService;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE)
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("배송 서비스: 결제 완료 이벤트 수신 - {}", event);

        try {
            // 1. 주문 정보 조회 (배송 주소 및 상품 정보 확인용)

            String trackingNumber = "TRACK_" + UUID.randomUUID().toString().substring(0, 8);
            String shippingAddress = "서울시 강남구 테헤란로 123"; // 임시 주소

            log.info("배송 시작: 주문ID={}, 운송장번호={}", event.getOrderId(), trackingNumber);

            // 주문 상태 업데이트
            orderService.updateOrder(event.getOrderId(),
                    OrderStatus.READY);

            // 배송 시작 이벤트 발행
            ShippingStartedEvent shippingEvent = ShippingStartedEvent.builder()
                    .orderId(event.getOrderId())
                    .shippingAddress(shippingAddress)
                    .trackingNumber(trackingNumber)
                    .timestamp(LocalDateTime.now())
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.SHIPPING_STARTED_ROUTING_KEY,
                    shippingEvent
            );

            log.info("배송 시작 이벤트 발행: {}", shippingEvent);

        } catch (Exception e) {
            Thread.currentThread().interrupt();
            log.error("배송 처리 중 오류 발생", e);
            throw new RuntimeException("배송 처리 실패", e);
        }
    }
}
