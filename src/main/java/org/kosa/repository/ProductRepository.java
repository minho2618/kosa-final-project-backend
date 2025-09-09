package org.kosa.repository;

import org.kosa.entity.Product;
import org.kosa.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 카테고리 별 상품 탐색(BUY-SRCH-001)
    Optional<List<Product>> findProductByCategory(ProductCategory category);

    // 상품 검색 기능(BUY-SRCH-002)
    @Query(value = "select p from Product p where p.name like %:name%")
    Optional<List<Product>> searchProductByName(String name);
    
    // 필터링 기능(BUY-SRCH-003)은 프론트에서 처리

    // 상품 상세 정보(BUY-PROD-001)
    Optional<Product> findProductByProductId(Long productId);
    
    // 장바구니 기능(BUY-PAY-001)은 프론트에서 처리
}