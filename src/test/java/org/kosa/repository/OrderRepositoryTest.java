package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.config.TestConfig;
import org.kosa.entity.Member;
import org.kosa.entity.Order;
import org.kosa.entity.OrderItem;
import org.kosa.entity.Product;
import org.kosa.enums.MemberRole;
import org.kosa.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestConfig
// @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private ProductRepository productRepository;

    private Member testMember;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        memberRepository.deleteAll();
        productRepository.deleteAll();
        
        testMember = Member.builder()
                .email("customer@example.com")
                .password("password")
                .role(MemberRole.ROLE_CUSTOMER)
                .name("Test Customer")
                .build();
        
        testMember = memberRepository.save(testMember);

        testProduct = Product.builder()
                .name("Test Product")
                .description("Test Product Description")
                .price(new BigDecimal("99.99"))
                .isActive(true)
                .build();
        
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @DisplayName("신규 주문 저장 테스트")
    void testSaveOrder() {
        // Given
        Order order = Order.builder()
                .member(testMember)
                .status(OrderStatus.PENDING)
                .address("Test Address 123")
                .build();

        // When
        Order savedOrder = orderRepository.save(order);

        // Then
        assertThat(savedOrder.getOrderId()).isNotNull();
        assertThat(savedOrder.getMember()).isEqualTo(testMember);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(savedOrder.getAddress()).isEqualTo("Test Address 123");
        assertThat(savedOrder.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("주문 ID로 주문 조회 테스트")
    void testFindByOrderId() {
        // Given
        Order order = Order.builder()
                .member(testMember)
                .status(OrderStatus.PENDING)
                .address("Test Address 123")
                .build();
        
        Order savedOrder = orderRepository.save(order);

        // When
        Optional<Order> foundOrder = orderRepository.findByOrderId(savedOrder.getOrderId());

        // Then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getOrderId()).isEqualTo(savedOrder.getOrderId());
        assertThat(foundOrder.get().getMember().getEmail()).isEqualTo("customer@example.com");
    }

    @Test
    @DisplayName("회원별 주문 전체 조회 테스트")
    void testFindAllByMember() {
        // Given
        Order order1 = Order.builder()
                .member(testMember)
                .status(OrderStatus.PENDING)
                .address("Address 1")
                .build();
        
        Order order2 = Order.builder()
                .member(testMember)
                .status(OrderStatus.READY)
                .address("Address 2")
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);

        // When
        Optional<List<Order>> orders = orderRepository.findAllByMember(testMember);

        // Then
        assertThat(orders).isPresent();
        assertThat(orders.get()).hasSize(2);
        assertThat(orders.get())
                .extracting("address")
                .containsExactlyInAnyOrder("Address 1", "Address 2");
    }

    @Test
    @DisplayName("기간별 회원 주문 조회 테스트")
    void testFindAllByMemberAndCreatedAtBetween() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        
        Order order1 = Order.builder()
                .member(testMember)
                .status(OrderStatus.PENDING)
                .address("Past Order")
                .build();
        
        Order order2 = Order.builder()
                .member(testMember)
                .status(OrderStatus.READY)
                .address("Current Order")
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);

        // When
        List<Order> orders = orderRepository.findAllByMemberAndCreatedAtBetween(testMember, start, end);

        // Then
        // All orders should be within the date range since they were created during test execution
        assertThat(orders).hasSize(2);
        assertThat(orders)
                .extracting("address")
                .containsExactlyInAnyOrder("Past Order", "Current Order");
    }

    @Test
    @DisplayName("주문 상태 업데이트 테스트")
    void testUpdateOrderStatus() {
        // Given
        Order order = Order.builder()
                .member(testMember)
                .status(OrderStatus.PENDING)
                .address("Test Address")
                .build();
        
        Order savedOrder = orderRepository.save(order);

        // When
        savedOrder.setStatus(OrderStatus.DONE);
        Order updatedOrder = orderRepository.save(savedOrder);

        // Then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.DONE);
    }

    @Test
    @DisplayName("주문에 주문 아이템 추가 테스트")
    void testOrderWithOrderItems() {
        // Given
        Order order = Order.builder()
                .member(testMember)
                .status(OrderStatus.PENDING)
                .address("Test Address")
                .build();
        
        OrderItem orderItem = OrderItem.builder()
                .product(testProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("99.99"))
                .totalPrice(new BigDecimal("199.98"))
                .build();

        order.getOrderItems().add(orderItem);
        orderItem.setOrder(order); // Set bidirectional relationship

        // When
        Order savedOrder = orderRepository.save(order);

        // Then
        assertThat(savedOrder.getOrderId()).isNotNull();
        assertThat(savedOrder.getOrderItems()).hasSize(1);
        assertThat(savedOrder.getOrderItems().get(0).getProduct().getName()).isEqualTo("Test Product");
        assertThat(savedOrder.getOrderItems().get(0).getQuantity()).isEqualTo(2);
    }
}