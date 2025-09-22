package org.kosa.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제 완료 이벤트 DTO
 * 결제가 성공적으로 처리되었을 때 발행되는 이벤트
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProcessedEvent {
    private Long orderId;           // 주문 ID
    private Long memberId;          // 회원 ID
    private BigDecimal totalAmount; // 결제 금액
    private String paymentId;       // 결제 시스템에서 생성된 결제 ID
    private String paymentMethod;   // 결제 수단 (CARD, BANK_TRANSFER 등)
    private LocalDateTime timestamp; // 이벤트 발생 시간
}
