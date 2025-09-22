package org.kosa.dto.order;

import lombok.*;
import org.kosa.dto.orderItem.OrderItemReq;
import org.kosa.dto.orderItem.OrderItemRes;
import org.kosa.entity.Member;
import org.kosa.entity.Order;
import org.kosa.entity.OrderItem;
import org.kosa.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
}
