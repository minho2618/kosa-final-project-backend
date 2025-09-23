package org.kosa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.config.RabbitMQConfig;
import org.kosa.dto.event.OrderCreatedEvent;
import org.kosa.dto.event.OrderItemEvent;
import org.kosa.dto.event.OrderStatusChangedEvent;
import org.kosa.dto.member.MemberRes;
import org.kosa.dto.order.OrderReq;
import org.kosa.dto.order.OrderRes;
import org.kosa.dto.orderItem.OrderItemReq;
import org.kosa.entity.Member;
import org.kosa.entity.Order;
import org.kosa.entity.OrderItem;
import org.kosa.enums.OrderStatus;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final CartService cartService;
    // RabbitMQ
    private final RabbitTemplate rabbitTemplate;

    // 주문하기
    @Transactional
    public Order createOrder(OrderReq orderReq) throws RecordNotFoundException {
        Order order = OrderReq.toOrder(orderReq);

        // 1. 주문 상태를 PENDING으로 초기화
        order.setStatus(OrderStatus.PENDING);

        // 2. DB에 주문상태 저장
        Order savedOrder = orderRepository.save(order);
        log.info("주문 생성됨: orderId={}, memberId={}",
                savedOrder.getOrderId(), savedOrder.getMember().getMemberId());

        // 3. 각 OrderItem의 totalPrice 구하기
        List<OrderItem> orderItemsList = savedOrder.getOrderItemList();
        orderItemsList.forEach((orderItem) -> {
            BigDecimal totalPrice = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            orderItem.setTotalPrice(totalPrice);
        });

        // 4. 주문 아이템들을 이벤트 DTO로 변환
        List<OrderItemEvent> orderItemEvents = orderItemsList
                .stream().map(this::convertToOrderItemEvent)
                .collect(Collectors.toList());

        // 5. 주문 생성 이벤트 객체 생성
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedOrder.getOrderId())
                .memberId(savedOrder.getMember().getMemberId())
                .memberEmail(savedOrder.getMember().getEmail()) // Member 엔터티에 email 필드 가정
                .address(savedOrder.getAddress())
                .orderItems(orderItemEvents)
                .timestamp(LocalDateTime.now())
                .build();

        // 6. RabbitMQ를 통해서 주문 생성 이벤트 발행
        // 재고 서비스에서 이 이벤트를 받아 재고 확인 프로세스 시작
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                event
        );

        log.info("주문 생성 이벤트 발행: orderId={}", savedOrder.getOrderId());
        return savedOrder;
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

        // 각 OrderItem의 totalPrice 구하기
        orderItems.forEach((orderItem) -> {
            BigDecimal totalPrice = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            orderItem.setTotalPrice(totalPrice);
        });

        // 주문 상태를 PENDING으로 초기화
        Order order = Order.builder()
                .member(member)
                .address(address)
                .orderItemList(orderItems)
                .status(OrderStatus.PENDING)
                .build();

        // DB에 주문상태 저장
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(memberId);

        // 주문 아이템들을 이벤트 DTO로 변환
        List<OrderItemEvent> orderItemEvents = orderItems
                .stream().map(this::convertToOrderItemEvent)
                .toList();

        // 주문 생성 이벤트 객체 생성
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedOrder.getOrderId())
                .memberId(savedOrder.getMember().getMemberId())
                .memberEmail(savedOrder.getMember().getEmail()) // Member 엔터티에 email 필드 가정
                .address(savedOrder.getAddress())
                .orderItems(orderItemEvents)
                .timestamp(LocalDateTime.now())
                .build();

        // RabbitMQ를 통해서 주문 생성 이벤트 발행
        // 재고 서비스에서 이 이벤트를 받아 재고 확인 프로세스 시작
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                event
        );

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

    /**
     * 주문 상태를 변경하고 상태 변경 이벤트를 발행
     *
     * @param orderId 상태를 변경할 주문 ID
     * @param newStatus 새로운 주문 상태
     */
    @Transactional
    public Order updateOrder(Long orderId, OrderStatus newStatus) {
        // 주문 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RecordNotFoundException("해당하는 주문이 존재하지 않습니다.", "NO ORDER"));

        // 이전 상태 보관 -> 이벤트 발행용
        OrderStatus previousStatus = order.getStatus();

        // 새로운 상태로 업데이트
        order.setStatus(newStatus);
        orderRepository.save(order);

        // 상태 변경 이벤트 객체 생성
        OrderStatusChangedEvent event = OrderStatusChangedEvent.builder()
                .orderId(order.getOrderId())
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .timestamp(LocalDateTime.now())
                .build();

        // RabbitMQ를 통해서 주문 생성 이벤트 발행
        // 재고 서비스에서 이 이벤트를 받아 재고 확인 프로세스 시작
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_STATUS_CHANGED_ROUTING_KEY,
                event
        );

        return order;
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    /**
     * OrderItem 엔티티를 OrderItemEvent DTO로 변환
     * 이벤트 발행시 필요한 정보만 추출하여 변환
     */
    private OrderItemEvent convertToOrderItemEvent(OrderItem orderItem) {
        return OrderItemEvent.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(orderItem.getProduct().getProductId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .discountValue(orderItem.getDiscountValue())
                .totalPrice(orderItem.getTotalPrice())
                .sellerId(orderItem.getProduct().getSeller().getMemberId()) // Seller가 Member를 상속한다고 가정
                .build();
    }
}