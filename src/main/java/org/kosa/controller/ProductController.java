package org.kosa.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.kosa.dto.product.ProductCardRes;
import org.kosa.dto.product.ProductReq;
import org.kosa.dto.product.ProductRes;
import org.kosa.enums.ProductCategory;
import org.kosa.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    // ===== 조회 =====

    /** 전체 목록 */
    @GetMapping
    public List<ProductRes> listAll() {
        return productService.getAllProducts();
    }

    /** 상품 상세 */
    @GetMapping("/{productId}")
    public ProductRes detail(@PathVariable Long productId) {
        return productService.getProductDetail(productId);
    }

    /** 카테고리별 카드 리스트 */
    @GetMapping("/category/{category}")
    public List<ProductCardRes> listByCategory(@PathVariable ProductCategory category) {
        return productService.getProductsByCategory(category);
    }

    /** 이름 검색 */
    @GetMapping("/search")
    public List<ProductRes> search(@RequestParam("keyword") String keyword) {
        return productService.searchProductsByName(keyword);
    }

    // ===== 생성 =====

    /** 상품 등록 */
    @PostMapping
    public ResponseEntity<ProductRes> create(@RequestBody @Valid ProductReq req) {
        ProductRes res = productService.createProduct(req);
        // ProductRes에 productId 게터가 있다고 가정
        return ResponseEntity
                .created(URI.create("/api/products/" + res.getProductId()))
                .body(res);
    }

    // ===== 수정/삭제 =====

    /** 부분 수정(PATCH): 전달된 필드만 반영 */
    @PatchMapping("/{productId}")
    public ProductRes update(@PathVariable Long productId, @RequestBody ProductReq req) {
        return productService.updateProduct(productId, req);
    }

    /** 삭제 */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    // ===== 편의 기능 =====

    /** 활성/비활성 토글(값 지정) */
    @PatchMapping("/{productId}/active")
    public ProductRes changeActive(@PathVariable Long productId, @RequestBody @Valid ActiveReq req) {
        return productService.changeActive(productId, req.isActive());
    }

    /** 가격 변경 */
    @PatchMapping("/{productId}/price")
    public ProductRes changePrice(@PathVariable Long productId, @RequestBody @Valid PriceReq req) {
        return productService.changePrice(productId, req.getPrice());
    }

    /** 할인 변경 */
    @PatchMapping("/{productId}/discount")
    public ProductRes changeDiscount(@PathVariable Long productId, @RequestBody @Valid DiscountReq req) {
        return productService.changeDiscount(productId, req.getDiscount());
    }

    // ===== 요청 DTO (원하면 별도 파일로 분리 가능) =====

    @Getter @Setter
    public static class ActiveReq {
        private boolean active;
    }

    @Getter @Setter
    public static class PriceReq {
        @NotNull
        @DecimalMin(value = "0.0", inclusive = true, message = "가격은 0 이상이어야 합니다.")
        private BigDecimal price;
    }

    @Getter @Setter
    public static class DiscountReq {
        @NotNull
        @DecimalMin(value = "0.0", inclusive = true, message = "할인은 0 이상이어야 합니다.")
        private BigDecimal discount;
    }
}

