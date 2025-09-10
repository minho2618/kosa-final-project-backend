package org.kosa.repository;

import org.kosa.entity.Order;
import org.kosa.entity.User;
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
    // User로 주문 조회
    Optional<List<Order>> findByUser(User user);

    Optional<Page<Order>> findByUser(User user, Pageable pageable);

    // 상태로 조회
    Optional<List<Order>> findByStatus(OrderStatus status);

    // 기간별 조회
    Optional<List<Order>> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Fetch Join으로 N+1 문제 해결
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems LEFT JOIN FETCH o.user WHERE o.user.userId = :userId")
    Optional<List<Order>> findByUserIdWithItems(@Param("userId") Long userId);

    // 복합 조건 조회
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.status = :status ORDER BY o.createdAt DESC")
    Optional<List<Order>> findByUserAndStatus(@Param("user") User user, @Param("status") OrderStatus status);
}