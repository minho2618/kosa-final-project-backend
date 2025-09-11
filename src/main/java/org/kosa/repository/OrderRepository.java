package org.kosa.repository;

import org.kosa.entity.Order;
import org.kosa.entity.Users;
import org.kosa.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 특정 주문 조회
    Optional<Order> findByOrderId(Long orderId);

    // 사용자의 전체 주문내역 조회
    Optional<List<Order>> findByUser(Users users);

//    Optional<Page<Order>> findByUsers(Users users, Pageable pageable);

//    // 상태로 조회
//    Optional<List<Order>> findByStatus(OrderStatus status);

    // 배송 조회(BUY-ORD-002)

    // 주문 취소 및 반품 요청(BUY-ORD-003)
}