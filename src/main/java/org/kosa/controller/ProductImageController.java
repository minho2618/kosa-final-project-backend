package org.kosa.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.productImage.ProductImageReq;
import org.kosa.dto.productImage.ProductImageRes;
import org.kosa.service.ProductImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/{productId}/images")
@Validated
public class ProductImageController {

    private final ProductImageService productImageService;

    /** 상품별 이미지 목록(정렬 순) */
    @GetMapping
    public List<ProductImageRes> list(@PathVariable Long productId) {
        return productImageService.listByProduct(productId);
    }

    /** 대표 이미지(정렬 1순위) */
    @GetMapping("/cover")
    public ProductImageRes cover(@PathVariable Long productId) {
        return productImageService.getCover(productId);
    }

    /** 단건 추가 (sortOrder 미지정 시 자동 다음 순번) */
    @PostMapping
    public ResponseEntity<ProductImageRes> add(@PathVariable Long productId,
                                               @RequestBody @Valid ProductImageReq req) {
        ProductImageRes res = productImageService.add(productId, req);
        return ResponseEntity
                .created(URI.create(String.format("/api/products/%d/images/%d", productId, res.getImageId())))
                .body(res);
    }

    /** 전체 교체(기존 삭제 후 새 목록으로) */
    @PostMapping("/replace")
    public List<ProductImageRes> replaceAll(@PathVariable Long productId,
                                            @RequestBody List<@Valid ProductImageReq> reqs) {
        return productImageService.replaceAll(productId, reqs);
    }

    /** 단건 수정 (URL/ALT/정렬) */
    @PutMapping("/{imageId}")
    public ProductImageRes update(@PathVariable Long productId,
                                  @PathVariable Long imageId,
                                  @RequestBody @Valid ProductImageReq req) {
        // productId는 경로 일치 검증용으로만 사용한다면, 서비스에 소유 검증 로직을 추가해도 좋습니다.
        return productImageService.update(imageId, req);
    }

    /** 지정 위치로 이동 (1-base index) */
    @PostMapping("/{imageId}/move/{pos}")
    public ResponseEntity<Void> move(@PathVariable Long productId,
                                     @PathVariable Long imageId,
                                     @PathVariable int pos) {
        productImageService.move(imageId, pos);
        return ResponseEntity.noContent().build();
    }

    /** 단건 삭제 */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(@PathVariable Long productId,
                                       @PathVariable Long imageId) {
        productImageService.delete(imageId);
        return ResponseEntity.noContent().build();
    }
}
