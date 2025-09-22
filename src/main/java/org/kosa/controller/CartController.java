package org.kosa.controller;

import lombok.RequiredArgsConstructor;
import org.kosa.security.CustomMemberDetails;
import org.kosa.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    // 상품을 장바구니에 추가합니다.
    @PostMapping("")
    public ResponseEntity<?> addProductToCart(
            @AuthenticationPrincipal CustomMemberDetails memberDetails,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        cartService.addProductToCart(memberDetails.getMember().getMemberId(), productId, quantity);
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
        return new ResponseEntity<>(cartService.getOrderItemsFromCart(memberDetails.getMember().getMemberId()), HttpStatus.OK);
    }

    // 장바구니의 총 가격을 조회합니다.
    @GetMapping("/total-price")
    public ResponseEntity<?> getCartTotalPrice(@AuthenticationPrincipal CustomMemberDetails memberDetails) {
        return new ResponseEntity<>(cartService.getCartTotalPrice(memberDetails.getMember().getMemberId()), HttpStatus.OK);
    }
}