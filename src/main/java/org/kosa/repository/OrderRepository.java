package org.kosa.repository;

import org.kosa.entity.Order;
import org.kosa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 특정 주문 조회
    Optional<Order> findByOrderId(Long orderId);

    // 사용자의 전체 주문내역 조회
    Optional<List<Order>> findByMember(Member member);
    // Optional<Page<Order>> findByMember(Member member, Pageable pageable);

//    // 상태로 조회
//    Optional<List<Order>> findByStatus(OrderStatus status);

    // 배송 조회(BUY-ORD-002)

    // 주문 취소 및 반품 요청(BUY-ORD-003)


}