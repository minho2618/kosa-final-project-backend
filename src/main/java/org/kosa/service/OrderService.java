package org.kosa.service;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.order.OrderReq;
import org.kosa.entity.Member;
import org.kosa.entity.Order;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;

    // 주문하기
    @Transactional
    public void createOrder(OrderReq orderReq) throws RecordNotFoundException {
        Order order = orderReq.toEntity();
        if (order == null)
            throw new RecordNotFoundException("해당하는 주문이 존재하지 않습니다.", "NO ORDER");

        orderRepository.save(order);
    }

    public Order findOrderById(Long orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 주문이 존재하지 않습니다.", "NO ORDER"));
    }

    public List<Order> findOrdersByMember(Long memberId) {
        Member member = memberService.findById(memberId);

        return orderRepository.findByMember(member)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 사용자가 존재하지 않습니다.", "NO MEMBER"));
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