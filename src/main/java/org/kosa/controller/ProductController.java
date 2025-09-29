package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.kosa.security.CustomMemberDetails;
import org.kosa.service.ProductService;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@Tag(name = "Product", description = "상품 관리 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "전체 상품 목록 조회", description = "시스템에 등록된 모든 상품 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ProductRes.class)))
    @GetMapping("")
    public List<ProductRes> listAll() {
        return productService.getAllProducts();
    }
    @Operation(summary = "상품 상세 정보 조회", description = "상품 ID를 이용하여 특정 상품의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ProductRes.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{productId}")
    public ProductRes detail(
            @Parameter(description = "조회할 상품의 ID", required = true, example = "1") @PathVariable Long productId) {
        return productService.getProductDetail(productId);
    }

    @Operation(summary = "카테고리별 상품 목록 조회", description = "특정 카테고리에 속하는 상품 목록을 카드 형태로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/category/{category}")
    public List<ProductCardRes> listByCategory(
            @Parameter(description = "조회할 상품 카테고리", required = true) @PathVariable ProductCategory category) {
        return productService.getProductsByCategory(category);
    }

    @Operation(summary = "상품 이름으로 검색", description = "키워드가 포함된 상품 이름으로 상품을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/search")
    public List<ProductRes> search(
            @Parameter(description = "검색할 키워드", required = true, example = "사과") @RequestParam("keyword") String keyword) {
        return productService.searchProductsByName(keyword);
    }

    @Operation(summary = "신규 상품 등록", description = "새로운 상품 정보를 시스템에 등록합니다.")
    @ApiResponse(responseCode = "201", description = "등록 성공")
    @PostMapping
    public ResponseEntity<ProductRes> create(
            @AuthenticationPrincipal CustomMemberDetails memberDetails,
            @RequestBody @Valid ProductReq req) {
        ProductRes res = productService.createProduct(req, memberDetails.getMember().getMemberId());
        return ResponseEntity
                .created(URI.create("/api/products/" + res.getProductId()))
                .body(res);
    }

    @Operation(summary = "상품 정보 부분 수정", description = "기존 상품의 정보를 부분적으로 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @PatchMapping("/{productId}")
    public ProductRes update(
            @Parameter(description = "수정할 상품의 ID", required = true) @PathVariable Long productId,
            @RequestBody ProductReq req) {
        return productService.updateProduct(productId, req);
    }

    @Operation(summary = "상품 삭제", description = "상품 정보를 시스템에서 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 상품의 ID", required = true) @PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상품 활성/비활성 상태 변경", description = "상품의 판매 상태를 활성 또는 비활성으로 변경합니다.")
    @ApiResponse(responseCode = "200", description = "상태 변경 성공")
    @PatchMapping("/{productId}/active")
    public ProductRes changeActive(
            @Parameter(description = "상태를 변경할 상품의 ID", required = true) @PathVariable Long productId,
            @RequestBody @Valid ActiveReq req) {
        return productService.changeActive(productId, req.isActive());
    }

    @Operation(summary = "상품 가격 변경", description = "상품의 판매 가격을 변경합니다.")
    @ApiResponse(responseCode = "200", description = "가격 변경 성공")
    @PatchMapping("/{productId}/price")
    public ProductRes changePrice(
            @Parameter(description = "가격을 변경할 상품의 ID", required = true) @PathVariable Long productId,
            @RequestBody @Valid PriceReq req) {
        return productService.changePrice(productId, req.getPrice());
    }

    @Operation(summary = "상품 할인액 변경", description = "상품의 할인 금액을 변경합니다.")
    @ApiResponse(responseCode = "200", description = "할인 변경 성공")
    @PatchMapping("/{productId}/discount")
    public ProductRes changeDiscount(
            @Parameter(description = "할인을 변경할 상품의 ID", required = true) @PathVariable Long productId,
            @RequestBody @Valid DiscountReq req) {
        return productService.changeDiscount(productId, req.getDiscount());
    }

    @Schema(description = "상품 활성 상태 요청 DTO")
    @Getter @Setter
    public static class ActiveReq {
        @Schema(description = "활성화 여부", example = "true")
        private boolean active;
    }

    @Schema(description = "상품 가격 변경 요청 DTO")
    @Getter @Setter
    public static class PriceReq {
        @NotNull
        @DecimalMin(value = "0.0", inclusive = true, message = "가격은 0 이상이어야 합니다.")
        @Schema(description = "새로운 가격", example = "15000.00")
        private BigDecimal price;
    }

    @Schema(description = "상품 할인액 변경 요청 DTO")
    @Getter @Setter
    public static class DiscountReq {
        @NotNull
        @DecimalMin(value = "0.0", inclusive = true, message = "할인은 0 이상이어야 합니다.")
        @Schema(description = "새로운 할인액", example = "1500.00")
        private BigDecimal discount;
    }
}