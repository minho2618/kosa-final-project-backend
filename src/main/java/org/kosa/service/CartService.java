package org.kosa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.cart.CartAddReq;
import org.kosa.dto.orderItem.OrderItemReq;
import org.kosa.entity.CartItem;
import org.kosa.entity.Product;
import org.kosa.repository.CartItemRepository;
import org.kosa.repository.ProductRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ObjectMapper objectMapper;

    private String getCartKey(Long memberId) {
        return "cart:user:" + memberId;
    }

    /*
     * 장바구니에 상품 추가
     * Redis의 Hash 자료구조를 사용해 'productId'와 'quantity'만 저장
     */
    public void addProductToCart(Long memberId, CartAddReq itemDetails) {
        String cartKey = getCartKey(memberId);
        String productIdStr = String.valueOf(itemDetails.getProductId());

        try {
            // 1. DTO를 JSON 문자열로 변환합니다. (이 JSON에 수량 및 상세 정보 모두 포함)
            String itemJson = objectMapper.writeValueAsString(itemDetails);

            // 2. Redis Hash에 JSON 문자열로 저장합니다. (HPUT 명령)
            // 상품 ID가 필드, JSON 문자열이 값이 됩니다.
            redisTemplate.opsForHash().put(cartKey, productIdStr, itemJson);

        } catch (Exception e) {
            throw new RuntimeException("Redis 장바구니 업데이트 중 오류 발생: " + e.getMessage(), e);
        }
    }

    /*
     * 회원의 장바구니 목록을 조회하고, OrderItem 생성을 위한 DTO 리스트로 반환
     * Redis에서 장바구니 데이터를 가져와 RDBMS에서 상품 상세 정보를 조회해 결합
     */
    public List<CartAddReq> getCartItemsFromCart(Long memberId) {
        Map<Object, Object> cartItems;

        try {
            cartItems = getCartDataFromRedis(memberId);
            if (cartItems.isEmpty()) throw new RuntimeException("Empty or Redis miss");

            // ⭐️ [핵심 수정] getOrderItemReqs 대신 JSON 파싱 결과를 바로 반환
            return parseCartDetailsFromRedis(cartItems, memberId);

        } catch (Exception e) {
            log.warn("Redis 오류, DB에서 장바구니 조회 fallback. memberId: {}", memberId);

            // ⚠️ DB 폴백 경로는 DB 데이터로 CartItemDetails를 구성하는 별도의 로직이 필요합니다.
            // 현재는 DB에서 상품 ID와 수량만 가져온 후, Product 상세 정보를 조회하여
            // CartItemDetails 리스트를 만들어야 합니다.

            cartItems = getCartDataFromDb(memberId);

            // Redis 복구 시 캐시 재삽입
            try {
                cacheCartToRedis(memberId, cartItems);
            } catch (Exception ex) {
                log.error("Redis에 장바구니 캐싱 실패: {}", ex.getMessage());
            }

            // DB 폴백 로직이 복잡하므로, 임시로 빈 리스트를 반환합니다.
            // 이 부분은 개발자님이 CartItemDetails를 구성하는 로직으로 대체해야 합니다.
            return new ArrayList<>();
        }

    }

    private Map<Object, Object> getCartDataFromDb(Long memberId) {
        List<CartItem> items = cartItemRepository.findByMemberId(memberId);
        Map<Object, Object> cart = new HashMap<>();
        for (CartItem item : items) {
            cart.put(item.getProductId().toString(), item.getQuantity());
        }
        return cart;
    }

    public void persistCartToDb(Long memberId) {
        Map<Object, Object> cartItems = getCartDataFromRedis(memberId);
        if (cartItems.isEmpty()) return;

        List<CartItem> cartItemEntities = cartItems.entrySet().stream()
                .map(e -> CartItem.builder()
                        .memberId(memberId)
                        .productId(Long.valueOf(e.getKey().toString()))
                        .quantity((int) e.getValue())
                        .build())
                .collect(Collectors.toList());

        // 기존 DB 장바구니는 삭제하고 새로 저장
        cartItemRepository.deleteByMemberId(memberId);
        cartItemRepository.saveAll(cartItemEntities);
    }

    private void cacheCartToRedis(Long memberId, Map<Object, Object> cartItems) {
        String cartKey = getCartKey(memberId);
        if (!cartItems.isEmpty()) {
            redisTemplate.opsForHash().putAll(cartKey, cartItems);
        }
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

    private List<CartAddReq> parseCartDetailsFromRedis(Map<Object, Object> cartItems, Long memberId) {
        String cartKey = getCartKey(memberId);
        List<CartAddReq> detailsList = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : cartItems.entrySet()) {
            Long productId = Long.valueOf(entry.getKey().toString());

            Object rawValue = entry.getValue();
            String itemJson = rawValue instanceof String ? (String) rawValue : new String((byte[]) rawValue);

            try {
                // ⭐️ JSON 문자열을 CartItemDetails DTO로 파싱
                CartAddReq detail = objectMapper.readValue(itemJson, CartAddReq.class);
                detailsList.add(detail);
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                // 파싱 오류 발생 시 해당 항목 삭제
                log.error("JSON 파싱 오류! Redis 데이터 손상 (productId: {}): {}", productId, itemJson, e);
                redisTemplate.opsForHash().delete(cartKey, entry.getKey());
            }
        }
        return detailsList;
    }
    // DTO 변환, 유효성 검사, Redis 동기화 로직
    private List<OrderItemReq> getOrderItemReqs(Map<Object, Object> cartItems, Map<Long, Product> productMap, Long memberId) {
        String cartKey = getCartKey(memberId);
        List<OrderItemReq> orderItemReqs = new ArrayList<>();

        for (Map.Entry<Object, Object> entry : cartItems.entrySet()) {
            Long productId = Long.valueOf(entry.getKey().toString());

            // 1. Redis에서 가져온 값을 String으로 변환 (설정에 따라 byte[]일 수 있음)
            Object rawValue = entry.getValue();
            // ⭐️ String으로 안전하게 변환
            String itemJson = rawValue instanceof String ? (String) rawValue : new String((byte[]) rawValue);

            CartAddReq itemDetails;

            // 2. ⭐️ [핵심] try-catch 블록으로 readValue 예외 처리
            try {
                itemDetails = objectMapper.readValue(itemJson, CartAddReq.class);

                // 3. 총액 계산 및 OrderItemReq 생성 (할인 필드 제외)
                BigDecimal totalPrice = itemDetails.getPrice().multiply(new BigDecimal(itemDetails.getQuantity()));

                OrderItemReq orderItemReq = OrderItemReq.builder()
                        .productId(itemDetails.getProductId())
                        .quantity(itemDetails.getQuantity())
                        .unitPrice(itemDetails.getPrice())
                        .discountValue(BigDecimal.ZERO)
                        .totalPrice(totalPrice)
                        .orderId(null)
                        .build();

                orderItemReqs.add(orderItemReq);

            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                // JSON 파싱 실패 시: (DTO 구조나 데이터 불일치)
                // ⭐️ 어떤 JSON 문자열이 문제를 일으켰는지 로그로 확인하는 것이 디버깅에 필수적입니다.
                log.error("JSON 파싱 오류! Redis 데이터: {}", itemJson);
                log.error("장바구니 항목 JSON 파싱 실패 (productId: {}): {}", productId, e.getMessage(), e);

                // 데이터 손상으로 간주하고 해당 항목 삭제
                redisTemplate.opsForHash().delete(cartKey, entry.getKey());
                continue; // 다음 항목으로 넘어갑니다.
            }
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
        String productIdStr = String.valueOf(productId);

        if (quantity <= 0) {
            // 수량이 0 이하면 항목 삭제
            redisTemplate.opsForHash().delete(cartKey, productIdStr);
            return;
        }

        // 1. Redis에서 기존 상품 정보를 JSON 문자열 형태로 가져옵니다.
        Object rawValue = redisTemplate.opsForHash().get(cartKey, productIdStr);

        if (rawValue == null) {
            // 🚨 해당 상품이 장바구니에 없으므로 업데이트 불가 (에러 처리 또는 무시)
            log.warn("장바구니에 없는 상품의 수량 업데이트 요청. memberId: {}, productId: {}", memberId, productId);
            return;
        }

        // 2. JSON 문자열로 변환 (Redis 설정에 따른 안전 장치)
        String itemJson = rawValue instanceof String ? (String) rawValue : new String((byte[]) rawValue);

        try {
            // 3. JSON을 CartAddReq DTO로 역직렬화
            CartAddReq existingReq = objectMapper.readValue(itemJson, CartAddReq.class);

            // 4. 수량 업데이트
            existingReq.setQuantity(quantity);

            // 5. 업데이트된 DTO를 다시 JSON 문자열로 직렬화
            String updatedJson = objectMapper.writeValueAsString(existingReq);

            // 6. Redis에 JSON 문자열로 저장
            redisTemplate.opsForHash().put(cartKey, productIdStr, updatedJson);

        } catch (Exception e) {
            // 파싱 또는 직렬화 중 오류 발생 시
            log.error("장바구니 수량 업데이트 중 JSON 처리 오류 발생. productId: {}", productId, e);
            // 오류난 항목을 삭제하거나, 예외를 던져야 할 수 있습니다.
            redisTemplate.opsForHash().delete(cartKey, productIdStr);
        }
    }

    public boolean isProductInCart(Long memberId, Long productId) {
        String cartKey = getCartKey(memberId);
        return redisTemplate.opsForHash().hasKey(cartKey, String.valueOf(productId));
    }

    public BigDecimal getCartTotalPrice(Long memberId) {
        // getCartItemsFromCart()의 반환 타입은 List<CartAddReq>로 가정됩니다.
        List<CartAddReq> cartDetailsList = getCartItemsFromCart(memberId);

        return cartDetailsList.stream()
                // ⭐️ [핵심 변경] 각 CartAddReq 항목의 (가격 * 수량)을 계산합니다.
                .map(req -> req.getPrice().multiply(new BigDecimal(req.getQuantity())))
                // 계산된 모든 항목의 총액을 합산합니다.
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}