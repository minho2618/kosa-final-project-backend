package org.kosa.dto.orderItem;

import lombok.*;
import org.kosa.entity.OrderItem;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class OrderItemRes {
    private Long orderItemId;
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountValue;
    private BigDecimal totalPrice;
    private Long orderId;

    public static OrderItemRes toOrderItemRes(OrderItem orderItem) {
        return OrderItemRes.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(orderItem.getProduct().getProductId())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .discountValue(orderItem.getDiscountValue())
                .totalPrice(orderItem.getTotalPrice())
                .orderId(orderItem.getOrder().getOrderId())
                .build();
    }
}
