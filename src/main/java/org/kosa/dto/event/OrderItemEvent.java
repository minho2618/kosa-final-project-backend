package org.kosa.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 주문 상품 이벤트 DTO
 * 주문에 포함된 각 상품의 상세 정보를 담는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEvent {
    private Long orderItemId;       // 주문 상품 ID
    private Long productId;         // 상품 ID
    private String productName;     // 상품명 (로그 및 알림용)
    private int quantity;           // 주문 수량
    private BigDecimal unitPrice;   // 단가
    private BigDecimal discountValue; // 할인 금액
    private BigDecimal totalPrice;  // 해당 상품의 총 금액
    private Long sellerId;          // 판매자 ID (배송 분리 처리용)
}
