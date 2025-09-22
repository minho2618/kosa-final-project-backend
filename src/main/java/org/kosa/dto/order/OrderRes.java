package org.kosa.dto.order;

import lombok.*;
import org.kosa.dto.member.MemberRes;
import org.kosa.dto.orderItem.OrderItemRes;
import org.kosa.entity.Order;
import org.kosa.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderRes {
    private Long orderId;
    private MemberRes memberRes;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String address;
    @Builder.Default
    private List<OrderItemRes> orderItemList = new ArrayList<>();

    public static OrderRes toOrderRes(Order order) {
        return OrderRes.builder()
                .orderId(order.getOrderId())
                .memberRes(MemberRes.toMemberRes(order.getMember()))
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .address(order.getAddress())
                .orderItemList(order.getOrderItemList()
                        .stream().map(OrderItemRes::toOrderItemRes)
                        .collect(Collectors.toList()))
                .build();
    }
}
