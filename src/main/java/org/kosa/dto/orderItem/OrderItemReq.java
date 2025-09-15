package org.kosa.dto.orderItem;

import lombok.*;
import org.kosa.entity.Order;
import org.kosa.entity.OrderItem;
import org.kosa.entity.Product;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class OrderItemReq {
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountValue;
    private BigDecimal totalPrice;
    private Long orderId;

    public OrderItem toOrderItem(OrderItemReq req) {
        return OrderItem.builder()
                .product(Product.builder().productId(req.getProductId()).build())
                .quantity(req.getQuantity())
                .unitPrice(req.getUnitPrice())
                .discountValue(req.getDiscountValue())
                .totalPrice(req.getTotalPrice())
                .order(Order.builder().orderId(req.getOrderId()).build())
                .build();
    }
}
