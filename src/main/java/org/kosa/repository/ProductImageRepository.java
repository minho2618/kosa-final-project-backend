package org.kosa.repository;

import org.kosa.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // 특정 상품 ID로 이미지 전체 조회
    List<ProductImage> findByProduct_ProductId(Long productId);

    // ===== BUY-PROD-001: 상세 이미지 노출(정렬) =====
    List<ProductImage> findByProduct_ProductIdOrderBySortOrderAsc(Long productId);

    // 대표 이미지 1건(정렬 순 최상위)
    Optional<ProductImage> findFirstByProduct_ProductIdOrderBySortOrderAsc(Long productId);


    // ===== SEL-PROD-004: 이미지 업로드/정렬 관리 =====
    // 특정 상품의 이미지 일괄 삭제(재업로드 시)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ProductImage pi where pi.product.productId = :productId")
    int deleteByProductId(Long productId);
}
