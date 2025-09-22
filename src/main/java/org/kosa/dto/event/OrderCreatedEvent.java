package org.kosa.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 생성 이벤트 DTO
 * 주문이 생성될 때 발행되는 이벤트로, 다른 서비스들이 후속 처리를 위해 필요한 정보를 포함
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {
    private Long orderId;           // 생성된 주문 ID
    private Long memberId;          // 주문한 회원 ID
    private String memberEmail;     // 회원 이메일 (알림 발송용)
    private String address;         // 배송 주소
    private List<OrderItemEvent> orderItems;  // 주문 상품 목록
    private BigDecimal totalAmount; // 주문 총 금액
    private LocalDateTime timestamp; // 이벤트 발생 시간
}
