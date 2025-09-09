package org.kosa.repository;

import org.kosa.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 주문 내역 확인(BUY-ORD-001)

    // 배송 조회(BUY-ORD-002)

    // 주문 취소 및 반품 요청(BUY-ORD-003)
}