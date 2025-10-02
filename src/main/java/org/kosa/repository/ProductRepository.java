package org.kosa.repository;

import org.kosa.dto.product.ProductCardRes;
import org.kosa.entity.Product;
import org.kosa.enums.ProductCategory;
import org.kosa.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 카테고리 별 상품 탐색(BUY-SRCH-001)
    Optional<List<Product>> findProductByCategory(ProductCategory category);

    // 상품 검색 기능(BUY-SRCH-002)
    @Query(value = "select p from Product p where p.name like %:name%")
    Optional<List<Product>> searchProductByName(String name);

    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE p.isActive = :isActive")
    List<Product> findProductByIsActive(@Param("isActive") boolean active);

    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE p.status = :status")
    List<Product> findProductByStatus(@Param("status") ProductStatus status);
    // 필터링 기능(BUY-SRCH-003)은 프론트에서 처리

    // 상품 상세 정보(BUY-PROD-001)
    Optional<Product> findProductByProductId(Long productId);

    @Query("""
        select new org.kosa.dto.product.ProductCardRes(
            p.productId,
            p.name,
            p.price,
            p.discountValue,
            p.category,
            coalesce(nullif(p.farmName,''), s.sellerName),
            (
              select pi1.url
              from ProductImage pi1
              where pi1.product = p
                and pi1.sortOrder = (
                  select min(pi2.sortOrder)
                  from ProductImage pi2
                  where pi2.product = p
                )
            )
        )
        from Product p
        join p.seller s
        where p.isActive = true
        order by p.createdAt desc
        """)
    Page<ProductCardRes> findProductCards(Pageable pageable);




    // 장바구니 기능(BUY-PAY-001)은 프론트에서 처리
    // -> save(entity) 기본 제공

}