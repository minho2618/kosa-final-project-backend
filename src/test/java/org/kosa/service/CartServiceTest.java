package org.kosa.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kosa.dto.orderItem.OrderItemReq;
import org.kosa.entity.Product;
import org.kosa.repository.ProductRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private CartService cartService;

    private final Long memberId = 1L;
    private final String cartKey = "cart:user:1";

    // @BeforeEach 메서드는 이제 비워둡니다. 각 테스트가 독립적으로 동작합니다.

    @Test
    @DisplayName("상품을 장바구니에 추가한다")
    void addProductToCart_success() {
        // Given
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        Long productId = 100L;
        int quantity = 5;

        // When
        cartService.addProductToCart(memberId, productId, quantity);

        // Then
        verify(hashOperations, times(1)).increment(cartKey, String.valueOf(productId), quantity);
    }

    //---------------------------------------------------------

    @Test
    @DisplayName("장바구니 상품 수량을 성공적으로 업데이트한다")
    void updateProductQuantity_success() {
        // Given
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        Long productId = 100L;
        int newQuantity = 3;

        // When
        cartService.updateProductQuantity(memberId, productId, newQuantity);

        // Then
        verify(hashOperations, times(1)).put(cartKey, String.valueOf(productId), newQuantity);
        verify(hashOperations, never()).delete(anyString(), anyString());
    }

    @Test
    @DisplayName("수량이 0 이하이면 장바구니에서 상품을 삭제한다")
    void updateProductQuantity_deleteWhenZeroOrLess() {
        // Given
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        Long productId = 100L;
        int newQuantity = 0;

        // When
        cartService.updateProductQuantity(memberId, productId, newQuantity);

        // Then
        verify(hashOperations, times(1)).delete(cartKey, String.valueOf(productId));
        verify(hashOperations, never()).put(anyString(), any(), any());
    }

    //---------------------------------------------------------

    @Test
    @DisplayName("장바구니에서 특정 상품을 삭제한다")
    void removeProductFromCart_success() {
        // Given
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        Long productId = 100L;

        // When
        cartService.removeProductFromCart(memberId, productId);

        // Then
        verify(hashOperations, times(1)).delete(cartKey, String.valueOf(productId));
    }

    //---------------------------------------------------------

    @Test
    @DisplayName("장바구니 전체를 비운다")
    void clearCart_success() {
        // Given - 이 테스트는 redisTemplate.delete()만 사용하므로
        // HashOperations에 대한 stubbing은 필요 없습니다.

        // When
        cartService.clearCart(memberId);

        // Then
        verify(redisTemplate, times(1)).delete(cartKey);
    }

    //---------------------------------------------------------

    @Test
    @DisplayName("장바구니에 상품이 존재하면 true를 반환한다")
    void isProductInCart_whenExists_returnsTrue() {
        // Given
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        Long productId = 100L;
        when(hashOperations.hasKey(cartKey, String.valueOf(productId))).thenReturn(true);

        // When
        boolean result = cartService.isProductInCart(memberId, productId);

        // Then
        assertTrue(result);
        verify(hashOperations, times(1)).hasKey(cartKey, String.valueOf(productId));
    }

    @Test
    @DisplayName("장바구니에 상품이 존재하지 않으면 false를 반환한다")
    void isProductInCart_whenNotExists_returnsFalse() {
        // Given
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        Long productId = 100L;
        when(hashOperations.hasKey(cartKey, String.valueOf(productId))).thenReturn(false);

        // When
        boolean result = cartService.isProductInCart(memberId, productId);

        // Then
        assertFalse(result);
        verify(hashOperations, times(1)).hasKey(cartKey, String.valueOf(productId));
    }

    //---------------------------------------------------------

    @Test
    @DisplayName("장바구니의 총 가격을 올바르게 계산한다")
    void getCartTotalPrice_success() {
        // Given
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        Map<Object, Object> cartItems = new HashMap<>();
        cartItems.put("100", 2);
        cartItems.put("101", 1);
        when(hashOperations.entries(cartKey)).thenReturn(cartItems);

        Product product1 = Product.builder().productId(100L).name("상품1").price(new BigDecimal("10000")).discountValue(new BigDecimal("0")).build();
        Product product2 = Product.builder().productId(101L).name("상품2").price(new BigDecimal("20000")).discountValue(new BigDecimal("0")).build();
        when(productRepository.findAllById(anyList())).thenReturn(List.of(product1, product2));

        // When
        BigDecimal totalPrice = cartService.getCartTotalPrice(memberId);

        // Then
        BigDecimal expectedTotal = new BigDecimal("40000"); // (10000 * 2) + (20000 * 1)
        assertEquals(expectedTotal, totalPrice);
    }

    // 이 테스트는 이전에 UnnecessaryStubbingException의 원인이었습니다.
    // 이제 when(redisTemplate.opsForHash())를 추가하여 문제를 해결합니다.
    @Test
    @DisplayName("장바구니 데이터를 성공적으로 조회하고 변환한다")
    void getOrderItemsFromCart_success() {
        // Given
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        Map<Object, Object> cartItems = new HashMap<>();
        cartItems.put("100", 2);
        cartItems.put("101", 1);
        when(hashOperations.entries(cartKey)).thenReturn(cartItems);

        Product product1 = Product.builder().productId(100L).name("상품1").price(new BigDecimal("10000")).discountValue(new BigDecimal("0")).build();
        Product product2 = Product.builder().productId(101L).name("상품2").price(new BigDecimal("20000")).discountValue(new BigDecimal("0")).build();
        when(productRepository.findAllById(anyList())).thenReturn(List.of(product1, product2));

        // When
        List<OrderItemReq> result = cartService.getOrderItemsFromCart(memberId);

        // Then
        assertEquals(2, result.size());
        assertEquals(100L, result.get(0).getProductId());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals(new BigDecimal("10000"), result.get(0).getUnitPrice());
    }
}