package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.*;
import org.kosa.enums.OrderStatus;
import org.kosa.enums.ProductCategory;
import org.kosa.enums.SellerRole;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderItemRepositoryTest {

    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private SellerRepository sellerRepository;

    private Order testOrder;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        Member user = memberRepository.save(Member.builder().username("order_user").role(MemberRole.ROLE_CUSTOMER).build());
        Member sellerUser = memberRepository.save(Member.builder().username("order_seller").role(MemberRole.ROLE_SELLER).build());
        Seller seller = sellerRepository.save(Seller.builder().member(sellerUser).sellerName("주문 농장").role(SellerRole.authenticated).build());

        testProduct1 = productRepository.save(Product.builder().name("주문 상품 1").price(new BigDecimal("100")).seller(seller).category(ProductCategory.기타).isActive(true).build());
        testProduct2 = productRepository.save(Product.builder().name("주문 상품 2").price(new BigDecimal("200")).seller(seller).category(ProductCategory.기타).isActive(true).build());

        testOrder = orderRepository.save(Order.builder().member(user).status(OrderStatus.PENDING).address("주소").build());

        orderItemRepository.save(OrderItem.builder().order(testOrder).product(testProduct1).quantity(5).totalPrice(new BigDecimal("500")).build());
        orderItemRepository.save(OrderItem.builder().order(testOrder).product(testProduct2).quantity(10).totalPrice(new BigDecimal("2000")).build());
    }

    @DisplayName("주문으로 주문상품들 조회하기")
    @Test
    void findByOrder() {
        // when
        List<OrderItem> orderItemList = orderItemRepository.findByOrder(testOrder).orElseThrow();

        // then
        assertThat(orderItemList).hasSize(2);
    }

    @Test
    @DisplayName("상품으로 주문상품 조회")
    void findByProduct() {
        // when
        List<OrderItem> items = orderItemRepository.findByProduct(testProduct1);

        // then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("수량으로 주문상품 조회")
    void findByQuantityGreaterThan() {
        // when
        List<OrderItem> items = orderItemRepository.findByQuantityGreaterThan(7);

        // then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getProduct().getName()).isEqualTo("주문 상품 2");
    }

    @Test
    @DisplayName("ID로 상품과 함께 조회(Fetch Join)")
    void findByIdWithProduct() {
        // given
        OrderItem item = orderItemRepository.findByProduct(testProduct1).get(0);

        // when
        OrderItem foundItem = orderItemRepository.findByIdWithProduct(item.getOrderItemId());

        // then
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getProduct().getName()).isEqualTo("주문 상품 1");
    }

    @Test
    @DisplayName("총 가격으로 주문상품 조회")
    void findByTotalPriceGreaterThanEqual() {
        // when
        List<OrderItem> items = orderItemRepository.findByTotalPriceGreaterThanEqual(1000);

        // then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getProduct().getName()).isEqualTo("주문 상품 2");
    }
}
