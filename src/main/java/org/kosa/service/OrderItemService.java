package org.kosa.service;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.orderItem.OrderItemReq;
import org.kosa.dto.orderItem.OrderItemRes;
import org.kosa.dto.product.ProductRes;
import org.kosa.entity.Order;
import org.kosa.entity.OrderItem;
import org.kosa.entity.Product;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;
    private final ProductService productService;

    @Transactional
    public Long createOrderItem(OrderItemReq orderItemReq) {
        OrderItem orderItem = orderItemReq.toEntity();
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return savedOrderItem.getOrderItemId();
    }

    public List<OrderItemRes> findOrderItemsByOrder(Long orderId) {
        Order order = orderService.findOrderById(orderId);

        List<OrderItem> orderItemList = orderItemRepository.findByOrder(order)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 주문이 존재하지 않습니다.", "NO ORDER"));

        return orderItemList.stream()
                .map(OrderItemRes::toOrderItemRes)
                .collect(Collectors.toList());
    }

    public List<OrderItemRes> findOrderItemsByProduct(Long productId) {
        ProductRes productRes = productService.getProductDetail(productId);
        Product product = Product.builder()
                .productId(productRes.getProductId())
                .name(productRes.getName())
                .description(productRes.getDescription())
                .price(productRes.getPrice())
                .category(productRes.getCategory())
                .discountValue(productRes.getDiscountValue())
                .isActive(productRes.getIsActive())
                .createdAt(productRes.getCreatedAt())
                .updatedAt(productRes.getUpdatedAt())
                .build();

        List<OrderItem> orderItemList = orderItemRepository.findByProduct(product);

        return orderItemList.stream()
                .map(OrderItemRes::toOrderItemRes)
                .collect(Collectors.toList());
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