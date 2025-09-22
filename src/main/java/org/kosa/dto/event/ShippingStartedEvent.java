package org.kosa.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 배송 시작 이벤트 DTO
 * 상품 배송이 시작되었을 때 발행되는 이벤트
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingStartedEvent {
    private Long orderId;           // 주문 ID
    private String shippingAddress; // 배송 주소
    private String trackingNumber;  // 운송장 번호
    private List<Long> productIds;  // 배송되는 상품 ID 목록
    private LocalDateTime estimatedDelivery; // 예상 배송 완료일
    private LocalDateTime timestamp; // 이벤트 발생 시간
}
