package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.cart.CartAddReq;
import org.kosa.dto.order.OrderReq;
import org.kosa.security.CustomMemberDetails;
import org.kosa.service.CartService;
import org.kosa.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    @Operation(summary = "결제 준비", description = "장바구니 내용을 기반으로 주문을 생성하고 결제 위젯 초기화에 필요한 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 생성 및 정보 반환 성공"),
            @ApiResponse(responseCode = "400", description = "장바구니 오류 또는 유효성 검증 실패"),
    })
    @PostMapping("/prepare-payment")
    public ResponseEntity<?> preparePayment(
            @Parameter(description = "주소, 결제 수단 등 추가 주문 정보") @RequestBody OrderReq orderReq,
            @AuthenticationPrincipal CustomMemberDetails memberDetails) {

        try {
            // OrderReq에서 필요한 정보 (예: 주소)를 추출하여 OrderService에 전달합니다.
            // memberId를 함께 전달하여 Redis 장바구니 데이터를 사용합니다.
            String address = orderReq.getAddress();

            // OrderService를 통해 DB에 주문을 저장하고 tossOrderId, totalAmount 등을 받습니다.
            Map<String, Object> paymentInfo = orderService.createOrderForPayment(memberDetails.getMember().getMemberId(), address);

            return ResponseEntity
                    .status(201)
                    .body(paymentInfo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("주문 생성 중 서버 오류: " + e.getMessage());
        }
    }

    @Operation(summary = "결제 최종 승인", description = "토스페이먼츠 결제 성공 후 리다이렉트되어 최종 결제를 승인합니다. 이 API는 사용자 브라우저에 의해 호출됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 승인 성공, 주문 완료 페이지로 리다이렉트"),
            @ApiResponse(responseCode = "500", description = "결제 승인 실패 또는 위변조 검증 실패"),
    })
    @GetMapping("/success")
    public String confirmPayment(
            @RequestParam String paymentKey,
            @RequestParam String orderId, // 이것은 DB에 저장된 tossOrderId입니다.
            @RequestParam Long amount) {

        try {
            // OrderService를 호출하여 토스페이먼츠 API로 최종 승인을 요청하고 DB 상태를 업데이트합니다.
            orderService.confirmPayment(paymentKey, orderId, amount);

            // ⭐️ 결제 성공 페이지로 리다이렉트 (프론트엔드 URL로 변경해야 함)
            return "redirect:/order/success?orderId=" + orderId;

        } catch (IllegalArgumentException e) {
            // 금액 불일치, 주문 ID 오류 등 위변조 및 검증 실패
            // ⭐️ 결제 실패 처리 페이지로 리다이렉트 (프론트엔드 URL로 변경해야 함)
            return "redirect:/order/fail?message=" + e.getMessage();
        } catch (Exception e) {
            // 토스 API 통신 오류, 서버 예외 등
            return "redirect:/order/fail?message=서버_처리_오류";
        }
    }

    // 상품을 장바구니에 추가합니다.
    @PostMapping("")
    public ResponseEntity<?> addProductToCart(
            @AuthenticationPrincipal CustomMemberDetails memberDetails,
            @RequestBody CartAddReq request) { // ⭐️ [수정] @RequestBody로 JSON 본문 수신

        cartService.addProductToCart(
                memberDetails.getMember().getMemberId(),
                request
        );
        return new ResponseEntity<>("Product added to cart successfully.", HttpStatus.CREATED);
    }

    // 장바구니 상품의 수량을 업데이트합니다.
    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProductQuantity(
            @AuthenticationPrincipal CustomMemberDetails memberDetails,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        cartService.updateProductQuantity(memberDetails.getMember().getMemberId(), productId, quantity);
        return new ResponseEntity<>("Product quantity updated successfully.", HttpStatus.ACCEPTED);
    }

    // 장바구니에서 특정 상품을 삭제합니다.
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeProductFromCart(
            @AuthenticationPrincipal CustomMemberDetails memberDetails,
            @PathVariable Long productId) {
        cartService.removeProductFromCart(memberDetails.getMember().getMemberId(), productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 장바구니 전체를 비웁니다.
    @DeleteMapping
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        cartService.clearCart(memberDetails.getMember().getMemberId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 장바구니에 담긴 모든 상품 목록을 조회합니다.
    @GetMapping
    public ResponseEntity<?> getCartItems(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        return new ResponseEntity<>(cartService.getCartItemsFromCart(memberDetails.getMember().getMemberId()), HttpStatus.OK);
    }

    // 장바구니의 총 가격을 조회합니다.
    @GetMapping("/total-price")
    public ResponseEntity<?> getCartTotalPrice(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        return new ResponseEntity<>(cartService.getCartTotalPrice(memberDetails.getMember().getMemberId()), HttpStatus.OK);
    }
}