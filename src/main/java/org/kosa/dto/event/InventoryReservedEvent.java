package org.kosa.dto.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 재고 확보 완료 이벤트 DTO
 * 모든 주문 상품의 재고가 확보되었을 때 발행되는 이벤트
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservedEvent {
    private Long orderId;           // 주문 ID
    private List<InventoryItemEvent> inventoryItems; // 재고 확보된 상품 목록
    private LocalDateTime timestamp; // 이벤트 발생 시간
}
