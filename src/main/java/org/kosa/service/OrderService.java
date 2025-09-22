package org.kosa.service;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.member.MemberRes;
import org.kosa.dto.order.OrderReq;
import org.kosa.dto.order.OrderRes;
import org.kosa.dto.orderItem.OrderItemReq;
import org.kosa.entity.Member;
import org.kosa.entity.Order;
import org.kosa.entity.OrderItem;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final CartService cartService;

    // 주문하기
    @Transactional
    public void createOrder(OrderReq orderReq) throws RecordNotFoundException {
        Order order = OrderReq.toOrder(orderReq);
        if (order == null)
            throw new RecordNotFoundException("해당하는 주문이 존재하지 않습니다.", "NO ORDER");

        orderRepository.save(order);
    }

    @Transactional
    public Order createOrderFromCart(Long memberId, String address) {
        List<OrderItemReq> orderItemReqs = cartService.getOrderItemsFromCart(memberId);
        if (orderItemReqs.isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어있습니다.");
        }
        Member member = memberService.findByMemberId(memberId);

        List<OrderItem> orderItems = orderItemReqs.stream()
                .map(OrderItemReq::toOrderItem)
                .toList();

        Order order = Order.builder()
                .member(member)
                .address(address)
                .orderItemList(orderItems)
                .build();

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(memberId);

        return savedOrder;
    }


    public Order findOrderById(Long orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 주문이 존재하지 않습니다.", "NO ORDER"));
    }

    public List<Order> findOrdersByMember(Long memberId) {
        Member member = memberService.findByMemberId(memberId);
        return orderRepository.findAllByMember(member)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 사용자가 존재하지 않습니다.", "NO MEMBER"));
    }

    // 연도별로 주문 목록 조회
    public List<OrderRes> findOrdersByYear(Long memberId, int year) {
        Member member = memberService.findByMemberId(memberId);

        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, 12, 31, 23, 59);

        return orderRepository.findAllByMemberAndCreatedAtBetween(member, start, end)
                .stream()
                .map(OrderRes::toOrderRes)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateOrder(Long orderId, OrderReq orderReq) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 주문이 존재하지 않습니다.", "NO ORDER"));

        order.setStatus(orderReq.getStatus());
        order.setAddress(orderReq.getAddress());

        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
}