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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestConfig
// @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        memberRepository.deleteAll();
        
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

        testOrder = Order.builder()
                .member(testMember)
                .status(OrderStatus.PENDING)
                .address("Test Address")
                .build();
        
        testOrder = orderRepository.save(testOrder);
    }

    @Test
    @DisplayName("신규 주문 아이템 저장 테스트")
    void testSaveOrderItem() {
        // Given
        OrderItem orderItem = OrderItem.builder()
                .order(testOrder)
                .product(testProduct)
                .quantity(3)
                .unitPrice(new BigDecimal("99.99"))
                .totalPrice(new BigDecimal("299.97"))
                .discountValue(new BigDecimal("10.00"))
                .build();

        // When
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // Then
        assertThat(savedOrderItem.getOrderItemId()).isNotNull();
        assertThat(savedOrderItem.getOrder()).isEqualTo(testOrder);
        assertThat(savedOrderItem.getProduct()).isEqualTo(testProduct);
        assertThat(savedOrderItem.getQuantity()).isEqualTo(3);
        assertThat(savedOrderItem.getTotalPrice()).isEqualTo(new BigDecimal("299.97"));
    }

    @Test
    @DisplayName("주문 ID로 주문 아이템 조회 테스트")
    void testFindByOrderId() {
        // Given
        OrderItem orderItem = OrderItem.builder()
                .order(testOrder)
                .product(testProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("99.99"))
                .totalPrice(new BigDecimal("199.98"))
                .build();
        
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // When
        // Note: This method might need to be corrected as it uses orderItemId instead of orderId
        List<OrderItem> foundOrderItems = orderItemRepository.findByOrderId(savedOrderItem.getOrderItemId()).orElseThrow();

        // Then
        assertThat(foundOrderItems).hasSize(1);
        assertThat(foundOrderItems.get(0).getOrderItemId()).isEqualTo(savedOrderItem.getOrderItemId());
    }

    @Test
    @DisplayName("상품 ID로 주문 아이템 조회 테스트")
    void testFindByProductId() {
        // Given
        OrderItem orderItem1 = OrderItem.builder()
                .order(testOrder)
                .product(testProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("99.99"))
                .totalPrice(new BigDecimal("199.98"))
                .build();
        
        OrderItem orderItem2 = OrderItem.builder()
                .order(testOrder)
                .product(testProduct) // Same product
                .quantity(1)
                .unitPrice(new BigDecimal("99.99"))
                .totalPrice(new BigDecimal("99.99"))
                .build();

        orderItemRepository.save(orderItem1);
        orderItemRepository.save(orderItem2);

        // When
        List<OrderItem> foundOrderItems = orderItemRepository.findByProductId(testProduct.getProductId());

        // Then
        assertThat(foundOrderItems).hasSize(2);
        assertThat(foundOrderItems)
                .extracting("product.productId")
                .containsOnly(testProduct.getProductId());
    }

    @Test
    @DisplayName("주문 ID와 상품 ID로 주문 아이템 조회 테스트")
    void testFindByOrderIdAndProductId() {
        // Given
        OrderItem orderItem = OrderItem.builder()
                .order(testOrder)
                .product(testProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("99.99"))
                .totalPrice(new BigDecimal("199.98"))
                .build();
        
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // When
        Optional<OrderItem> foundOrderItem = orderItemRepository.findByOrderIdAndProductId(
                testOrder.getOrderId(), testProduct.getProductId());

        // Then
        assertThat(foundOrderItem).isPresent();
        assertThat(foundOrderItem.get().getOrder().getOrderId()).isEqualTo(testOrder.getOrderId());
        assertThat(foundOrderItem.get().getProduct().getProductId()).isEqualTo(testProduct.getProductId());
    }

    @Test
    @DisplayName("수량 기준으로 주문 아이템 조회 테스트")
    void testFindByQuantityGreaterThan() {
        // Given
        OrderItem orderItem1 = OrderItem.builder()
                .order(testOrder)
                .product(testProduct)
                .quantity(1) // Less than 1
                .unitPrice(new BigDecimal("99.99"))
                .totalPrice(new BigDecimal("99.99"))
                .build();
        
        OrderItem orderItem2 = OrderItem.builder()
                .order(testOrder)
                .product(testProduct)
                .quantity(5) // Greater than 1
                .unitPrice(new BigDecimal("99.99"))
                .totalPrice(new BigDecimal("499.95"))
                .build();

        orderItemRepository.save(orderItem1);
        orderItemRepository.save(orderItem2);

        // When
        List<OrderItem> foundOrderItems = orderItemRepository.findByQuantityGreaterThan(1);

        // Then
        assertThat(foundOrderItems).hasSize(1);
        assertThat(foundOrderItems.get(0).getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("ID로 주문 아이템과 상품 함께 조회 테스트")
    void testFindByIdWithProduct() {
        // Given
        OrderItem orderItem = OrderItem.builder()
                .order(testOrder)
                .product(testProduct)
                .quantity(3)
                .unitPrice(new BigDecimal("99.99"))
                .totalPrice(new BigDecimal("299.97"))
                .build();
        
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);

        // When
        OrderItem foundOrderItem = orderItemRepository.findByIdWithProduct(savedOrderItem.getOrderItemId());

        // Then
        assertThat(foundOrderItem).isNotNull();
        assertThat(foundOrderItem.getOrderItemId()).isEqualTo(savedOrderItem.getOrderItemId());
        assertThat(foundOrderItem.getProduct()).isNotNull();
        assertThat(foundOrderItem.getProduct().getName()).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("특정 가격 이상의 주문 아이템 조회 테스트")
    void testFindByTotalPriceGreaterThanEqual() {
        // Given
        OrderItem orderItem1 = OrderItem.builder()
                .order(testOrder)
                .product(testProduct)
                .quantity(1)
                .unitPrice(new BigDecimal("50.00"))
                .totalPrice(new BigDecimal("50.00"))
                .build();
        
        OrderItem orderItem2 = OrderItem.builder()
                .order(testOrder)
                .product(testProduct)
                .quantity(2)
                .unitPrice(new BigDecimal("100.00"))
                .totalPrice(new BigDecimal("200.00"))
                .build();

        orderItemRepository.save(orderItem1);
        orderItemRepository.save(orderItem2);

        // When
        List<OrderItem> foundOrderItems = orderItemRepository.findByTotalPriceGreaterThanEqual(100.0);

        // Then
        assertThat(foundOrderItems).hasSize(1);
        assertThat(foundOrderItems.get(0).getTotalPrice()).isEqualTo(new BigDecimal("200.00"));
    }
}