package org.kosa.dto.product;

import lombok.*;
import org.kosa.dto.productImage.ProductImageRes;
import org.kosa.dto.seller.SellerRes;
import org.kosa.entity.Product;
import org.kosa.entity.ProductImage;
import org.kosa.entity.Seller;
import org.kosa.enums.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailRes {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductCategory category;
    private BigDecimal discountValue;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private SellerRes seller; // 판매자 정보
    private List<ProductImageRes> images; // 전체 이미지 목록

    public static ProductDetailRes from(Product product, List<ProductImage> images, Seller seller) {
        return ProductDetailRes.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .discountValue(product.getDiscountValue())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .seller(SellerRes.from(seller))
                .images(images.stream()
                        .map(ProductImageRes::from)
                        .collect(Collectors.toList()))
                .build();
    }
}