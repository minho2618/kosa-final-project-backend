package org.kosa.dto.order;

import lombok.*;
import org.kosa.entity.Member;
import org.kosa.entity.Order;
import org.kosa.entity.OrderItem;
import org.kosa.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class OrderReq {
    private Long memberId;
    private OrderStatus status;
    private String address;
    private List<OrderItem> orderItemList = new ArrayList<>();

    public static Order toOrder(OrderReq orderReq) {
        return Order.builder()
                .member(Member.builder().memberId(orderReq.getMemberId()).build())
                .status(orderReq.getStatus())
                .address(orderReq.getAddress())
                .orderItemList(orderReq.getOrderItemList())
                .build();
    }

    public Order toEntity() {
        return Order.builder()
                .member(Member.builder().memberId(this.getMemberId()).build())
                .status(this.getStatus())
                .address(this.getAddress())
                .orderItemList(this.getOrderItemList())
                .build();
    }
}
