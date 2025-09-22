package org.kosa.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 재고 처리 결과 DTO
 * 각 상품별 재고 확보 결과를 담는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItemEvent {
    private Long productId;         // 상품 ID
    private int requestedQuantity;  // 요청된 수량
    private int reservedQuantity;   // 실제 확보된 수량
    private boolean isAvailable;    // 재고 확보 성공 여부
}
