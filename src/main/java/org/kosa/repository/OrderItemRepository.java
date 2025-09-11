package org.kosa.repository;

import org.kosa.entity.Order;
import org.kosa.entity.OrderItem;
import org.kosa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // 주문으로 조회
    Optional<List<OrderItem>> findByOrder(Order order);

    // Product로 조회
    List<OrderItem> findByProduct(Product product);

    // 수량 범위로 조회
    List<OrderItem> findByQuantityGreaterThan(int quantity);

    // Product와 함께 조회 (N+1 방지)
    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.product WHERE oi.orderItemId = :id")
    OrderItem findByIdWithProduct(@Param("id") Long id);

    // 특정 가격 이상의 주문 항목
    @Query("SELECT oi FROM OrderItem oi WHERE oi.totalPrice >= :price")
    List<OrderItem> findByTotalPriceGreaterThanEqual(@Param("price") double price);
}