package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.entity.Order;
import org.kosa.entity.Users;
import org.kosa.enums.OrderStatus;
import org.kosa.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UsersRepository usersRepository;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = usersRepository.save(Users.builder().username("main_order_user").role(UserRole.ROLE_CUSTOMER).build());

        orderRepository.save(Order.builder().user(testUser).status(OrderStatus.PENDING).address("주소1").build());
        orderRepository.save(Order.builder().user(testUser).status(OrderStatus.DONE).address("주소2").build());
    }

    @Test
    @DisplayName("사용자로 전체 주문 목록 조회")
    void findByUser() {
        // when
        List<Order> orderList = orderRepository.findByUser(testUser).orElseThrow();

        // then
        assertThat(orderList).hasSize(2);
    }

    @Test
    @DisplayName("주문 ID로 특정 주문 조회")
    void findByOrderId() {
        // given
        Order newOrder = orderRepository.save(Order.builder().user(testUser).status(OrderStatus.DONE).address("주소3").build());

        // when
        Order foundOrder = orderRepository.findByOrderId(newOrder.getOrderId()).orElseThrow();

        // then
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.DONE);
        assertThat(foundOrder.getAddress()).isEqualTo("주소3");
    }
}
