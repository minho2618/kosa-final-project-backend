package org.kosa.dto.orderItem;

import lombok.*;
import org.kosa.entity.OrderItem;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRes {
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountValue;
    private BigDecimal totalPrice;

    public static CartItemRes toCartItemRes(OrderItem orderItem){
        return CartItemRes.builder()
                .productId(orderItem.getProduct().getProductId())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .discountValue(orderItem.getDiscountValue())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }
}