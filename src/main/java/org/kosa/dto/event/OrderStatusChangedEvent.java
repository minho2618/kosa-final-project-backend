package org.kosa.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kosa.enums.OrderStatus;

import java.time.LocalDateTime;

/**
 * 주문 상태 변경 이벤트 DTO
 * 주문 상태가 변경될 때마다 발행되는 이벤트 (로깅 및 알림용)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusChangedEvent {
    private Long orderId;           // 주문 ID
    private OrderStatus previousStatus; // 이전 상태
    private OrderStatus newStatus;  // 새로운 상태
    private String reason;          // 상태 변경 사유
    private LocalDateTime timestamp; // 이벤트 발생 시간
}
