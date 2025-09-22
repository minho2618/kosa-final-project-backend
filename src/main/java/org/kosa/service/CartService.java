package org.kosa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.orderItem.OrderItemReq;
import org.kosa.entity.Product;
import org.kosa.repository.ProductRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;

    private String getCartKey(Long memberId) {
        return "cart:user:" + memberId;
    }

    /*
     * 장바구니에 상품 추가
     * Redis의 Hash 자료구조를 사용해 'productId'와 'quantity'만 저장
     */
    public void addProductToCart(Long memberId, Long productId, int quantity) {
        String cartKey = getCartKey(memberId);
        redisTemplate.opsForHash().increment(cartKey, String.valueOf(productId), quantity);
    }

    /*
     * 회원의 장바구니 목록을 조회하고, OrderItem 생성을 위한 DTO 리스트로 반환
     * Redis에서 장바구니 데이터를 가져와 RDBMS에서 상품 상세 정보를 조회해 결합
     */
    public List<OrderItemReq> getOrderItemsFromCart(Long memberId) {
        Map<Object, Object> cartItems = getCartDataFromRedis(memberId);
        if (cartItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> products = getProductsFromRdb(cartItems);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, p -> p));

        return getOrderItemReqs(cartItems, productMap, memberId);
    }

    // Redis에서 장바구니 데이터를 조회하는 역할
    private Map<Object, Object> getCartDataFromRedis(Long memberId) {
        String cartKey = getCartKey(memberId);
        return redisTemplate.opsForHash().entries(cartKey);
    }

    // RDBMS에서 상품 정보만 조회하는 역할
    private List<Product> getProductsFromRdb(Map<Object, Object> cartItems) {
        List<Long> productIds = cartItems.keySet().stream()
                .map(key -> Long.valueOf(key.toString()))
                .collect(Collectors.toList());
        return productRepository.findAllById(productIds);
    }

    // DTO 변환, 유효성 검사, Redis 동기화 로직
    private List<OrderItemReq> getOrderItemReqs(Map<Object, Object> cartItems, Map<Long, Product> productMap, Long memberId) {
        String cartKey = getCartKey(memberId);
        List<OrderItemReq> orderItemReqs = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : cartItems.entrySet()) {
            Long productId = Long.valueOf(entry.getKey().toString());
            int quantity = (int) entry.getValue();
            Product product = productMap.get(productId);
            if (product == null) {
                redisTemplate.opsForHash().delete(cartKey, String.valueOf(productId));
                continue;
            }

            BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(quantity))
                    .subtract(product.getDiscountValue());

            OrderItemReq orderItemReq = OrderItemReq.builder()
                    .productId(product.getProductId())
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .discountValue(product.getDiscountValue())
                    .totalPrice(totalPrice)
                    .orderId(null)
                    .build();

            orderItemReqs.add(orderItemReq);
        }
        return orderItemReqs;
    }

    // 장바구니에서 특정 상품을 삭제
    public void removeProductFromCart(Long memberId, Long productId) {
        String cartKey = getCartKey(memberId);
        redisTemplate.opsForHash().delete(cartKey, String.valueOf(productId));
    }

    // 장바구니 전체 비우기
    public void clearCart(Long memberId) {
        String cartKey = getCartKey(memberId);
        redisTemplate.delete(cartKey);
    }

    public void updateProductQuantity(Long memberId, Long productId, int quantity) {
        String cartKey = getCartKey(memberId);
        if (quantity <= 0) redisTemplate.opsForHash().delete(cartKey, String.valueOf(productId));
        else redisTemplate.opsForHash().put(cartKey, String.valueOf(productId), quantity);
    }

    public boolean isProductInCart(Long memberId, Long productId) {
        String cartKey = getCartKey(memberId);
        return redisTemplate.opsForHash().hasKey(cartKey, String.valueOf(productId));
    }

    public BigDecimal getCartTotalPrice(Long memberId) {
        List<OrderItemReq> orderItemReqs = getOrderItemsFromCart(memberId);
        return orderItemReqs.stream()
                .map(OrderItemReq::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}