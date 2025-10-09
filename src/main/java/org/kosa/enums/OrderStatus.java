package org.kosa.enums;

public enum OrderStatus {
    PENDING,    // 1. 주문 생성 (결제 대기)
    PAID,       // 2. 결제 승인 성공 (confirmPayment 성공)
    READY,      // 3. 상품 준비 중 (PAID 후 내부 프로세스)
    DONE,       // 4. 주문 완료
    CANCELLED,  // 주문 취소
    FAILED      // 결제 실패
}
