package org.kosa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.config.RabbitMQConfig;
import org.kosa.dto.cart.CartAddReq;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final CartService cartService;
    @Value("${tosspayments.secretKey}") // application.yml/properties에서 설정한 시크릿 키
    private String secretKey;
    private final RestTemplate restTemplate;
    private final ProductService productService;

    // RabbitMQ
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public Map<String, Object> createOrderForPayment(Long memberId, String address) {

        // 1. 장바구니에서 상세 정보(CartAddReq) 리스트를 가져옵니다.
        // ⚠️ cartService.getOrderItemsFromCart()의 반환 타입이 List<CartAddReq>로 가정됩니다.
        List<CartAddReq> cartDetailsList = cartService.getCartItemsFromCart(memberId);

        if (cartDetailsList.isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어있습니다.");
        }

        Member member = memberService.findByMemberId(memberId);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 2. 총 금액 계산 및 OrderItem 객체 생성 (CartAddReq 기반)
        for (CartAddReq req : cartDetailsList) { // ⭐️ [수정] CartAddReq 사용

            // 상품 가격 및 수량 추출
            BigDecimal unitPrice = req.getPrice();
            int quantity = req.getQuantity();

            // 2-1. 단일 항목 총 금액 계산
            BigDecimal itemTotalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

            // 2-2. 전체 총 금액에 합산
            totalAmount = totalAmount.add(itemTotalPrice);

            // 2-3. OrderItem 객체 생성
            // ⭐️ [핵심 변경] OrderItemReq.toOrderItem(req) 대신 CartAddReq 기반으로 OrderItem 생성
            OrderItem orderItem = OrderItem.builder()
                    .product(productService.getProduct(req.getProductId()))
                    .unitPrice(unitPrice)
                    .quantity(quantity)
                    // discountValue가 없으므로 생략하거나 0으로 설정
                    .totalPrice(itemTotalPrice)
                    .build();

            orderItems.add(orderItem);
        }

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("결제 금액이 0원입니다.");
        }

        // 3. 주소 검증 및 기본 주소 사용 (기존 로직 유지)
        String finalAddress = address;

        if (finalAddress == null || finalAddress.trim().isEmpty()) {
            String defaultAddress = member.getAddress();

            if (defaultAddress == null || defaultAddress.trim().isEmpty()) {
                throw new IllegalArgumentException("배송지 주소가 지정되지 않았습니다. 주문 정보를 입력하거나 기본 주소를 설정해주세요.");
            }
            finalAddress = defaultAddress;
        }

        // 4. Order 엔티티 생성 및 DB 저장 (기존 로직 유지)
        String tossOrderId = UUID.randomUUID().toString();

        Order order = Order.builder()
                .tossOrderId(tossOrderId)
                .totalAmount(totalAmount)
                .member(member)
                .address(finalAddress)
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        // OrderItem에 Order 연결 및 Order에 OrderItem List 연결 (기존 로직 유지)
        for (OrderItem item : orderItems) {
            item.setOrder(savedOrder);
        }
        savedOrder.setOrderItemList(orderItems);

        // 5. RabbitMQ 이벤트 발행 (기존 로직 유지)
        List<OrderItemEvent> orderItemEvents = orderItems
                .stream().map(this::convertToOrderItemEvent)
                .toList();

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedOrder.getOrderId())
                .memberId(savedOrder.getMember().getMemberId())
                .memberEmail(savedOrder.getMember().getEmail())
                .address(savedOrder.getAddress())
                .orderItems(orderItemEvents)
                .timestamp(LocalDateTime.now())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                event
        );

        // 6. 프론트엔드 반환 정보 (기존 로직 유지)
        Map<String, Object> paymentInfo = new HashMap<>();
        paymentInfo.put("tossOrderId", tossOrderId);
        paymentInfo.put("totalAmount", totalAmount.longValue());
        paymentInfo.put("customerKey", memberId.toString());

        return paymentInfo;
    }

    /**
     * 토스페이먼츠 서버로 최종 결제 승인 요청을 보냅니다.
     */
    @Transactional
    public void confirmPayment(String paymentKey, String orderId, Long amount) {

        Order order = orderRepository.findByTossOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 ID(" + orderId + ")가 DB에 존재하지 않습니다."));

        // 금액 위변조 검증
        if (order.getTotalAmount().longValue() != amount) {
            log.error("금액 불일치 감지. DB금액: {}, 받은 금액: {}", order.getTotalAmount(), amount);
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentKey", paymentKey);
        payload.put("orderId", orderId);
        payload.put("amount", amount);

        String auth = secretKey + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String url = "https://api.tosspayments.com/v1/payments/confirm";
            RequestEntity<Map<String, Object>> request = new RequestEntity<>(
                    payload, headers, HttpMethod.POST, new java.net.URI(url));

            ResponseEntity<String> response = restTemplate.exchange(request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                order.setStatus(OrderStatus.PAID);
                order.setPaymentKey(paymentKey);
                orderRepository.save(order);
            } else {
                log.error("토스 결제 승인 API 실패. 응답: {}", response.getBody());
                order.setStatus(OrderStatus.FAILED);
                orderRepository.save(order);
                throw new RuntimeException("토스 API 결제 승인 실패");
            }
        } catch (Exception e) {
            log.error("결제 승인 통신 오류 발생", e);
            throw new RuntimeException("결제 승인 과정 중 오류 발생", e);
        }
    }

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
        // 1. 장바구니에서 상세 정보(CartAddReq) 리스트를 가져옵니다.
        // cartService.getCartItemsFromCart()의 반환 타입은 List<CartAddReq>여야 합니다.
        List<CartAddReq> cartDetailsList = cartService.getCartItemsFromCart(memberId);

        if (cartDetailsList.isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어있습니다.");
        }
        Member member = memberService.findByMemberId(memberId);

        // 2. CartAddReq를 OrderItem 엔티티로 변환합니다.
        List<OrderItem> orderItems = cartDetailsList.stream()
                // ⭐️ CartAddReq의 정보를 이용해 OrderItem 생성
                .map(request -> OrderItem.builder()
                        .product(productService.getProduct(request.getProductId()))
                        .unitPrice(request.getPrice())
                        .quantity(request.getQuantity())
                        .build())
                .toList();

        // 3. 각 OrderItem의 totalPrice를 계산하고 설정합니다.
        orderItems.forEach((orderItem) -> {
            // unitPrice * quantity (할인 값이 0이므로 반영하지 않음)
            BigDecimal totalPrice = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            orderItem.setTotalPrice(totalPrice);
        });

        // 4. 주문 정보 생성 및 DB 저장
        Order order = Order.builder()
                .member(member)
                .address(address)
                .orderItemList(orderItems)
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderRepository.save(order);

        // 5. 장바구니 비우기
        cartService.clearCart(memberId);

        // 6. 주문 아이템들을 이벤트 DTO로 변환
        List<OrderItemEvent> orderItemEvents = orderItems
                .stream().map(this::convertToOrderItemEvent)
                .toList();

        // 7. 주문 생성 이벤트 객체 생성
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedOrder.getOrderId())
                .memberId(savedOrder.getMember().getMemberId())
                .memberEmail(savedOrder.getMember().getEmail()) // Member 엔터티에 email 필드 가정
                .address(savedOrder.getAddress())
                .orderItems(orderItemEvents)
                .timestamp(LocalDateTime.now())
                .build();

        // 8. RabbitMQ를 통해서 주문 생성 이벤트 발행
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