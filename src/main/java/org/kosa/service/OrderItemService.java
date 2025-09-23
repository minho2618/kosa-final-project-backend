package org.kosa.service;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.orderItem.OrderItemReq;
import org.kosa.dto.orderItem.OrderItemRes;
import org.kosa.dto.product.ProductRes;
import org.kosa.entity.Order;
import org.kosa.entity.OrderItem;
import org.kosa.entity.Product;
import org.kosa.exception.InvalidInputException;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.OrderItemRepository;
import org.kosa.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Transactional
    public Long createOrderItem(OrderItemReq orderItemReq) {
        OrderItem orderItem = OrderItemReq.toOrderItem(orderItemReq);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return savedOrderItem.getOrderItemId();
    }

    public List<OrderItemRes> findOrderItemsByOrder(Long orderId) {
        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 주문이 존재하지 않습니다.", "NO ORDER"));
        return orderItemList.stream()
                .map(OrderItemRes::toOrderItemRes)
                .collect(Collectors.toList());
    }

    /*public List<OrderItemRes> findOrderItemsByProduct(Long productId) {
        Product product = productRepository.findById(productId)   // PK로 조회(Optional)
                .orElseThrow(() -> new InvalidInputException(
                        "상품을 찾을 수 없습니다. id=" + productId, "Not Found"));
        List<OrderItem> orderItemList = orderItemRepository.findByProduct(product);

        return orderItemList.stream()
                .map(OrderItemRes::toOrderItemRes)
                .collect(Collectors.toList());
    }*/

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