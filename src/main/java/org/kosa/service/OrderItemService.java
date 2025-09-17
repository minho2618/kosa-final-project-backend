package org.kosa.service;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.orderItem.OrderItemReq;
import org.kosa.entity.Order;
import org.kosa.entity.OrderItem;
import org.kosa.entity.Product;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Long createOrderItem(OrderItemReq orderItemReq) {
        OrderItem orderItem = orderItemReq.toEntity();
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return savedOrderItem.getOrderItemId();
    }

    public List<OrderItem> findOrderItemsByOrder(Order order) {
        return orderItemRepository.findByOrder(order)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 주문이 존재하지 않습니다.", "NO ORDER"));
    }

    public List<OrderItem> findOrderItemsByProduct(Product product) {
        return orderItemRepository.findByProduct(product);
    }

    public List<OrderItem> findByQuantityGreaterThan(int quantity) {
        return orderItemRepository.findByQuantityGreaterThan(quantity);
    }

    public OrderItem findByIdWithProduct(Long id) {
        return orderItemRepository.findByIdWithProduct(id);
    }

    public List<OrderItem> findByTotalPriceGreaterThanEqual(double price) {
        return orderItemRepository.findByTotalPriceGreaterThanEqual(price);
    }

    @Transactional
    public void updateOrderItem(Long orderItemId, OrderItemReq orderItemReq) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 주문이 존재하지 않습니다.", "NO ORDER"));

        orderItem.setQuantity(orderItemReq.getQuantity());
        orderItem.setUnitPrice(orderItemReq.getUnitPrice());
        orderItem.setDiscountValue(orderItemReq.getDiscountValue());
        orderItem.setTotalPrice(orderItemReq.getTotalPrice());

        orderItemRepository.save(orderItem);
    }

    @Transactional
    public void deleteOrderItem(Long orderItemId) {
        orderItemRepository.deleteById(orderItemId);
    }
}