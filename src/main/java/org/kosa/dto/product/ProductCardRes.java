package org.kosa.dto.product;

import lombok.*;
import org.kosa.entity.Product;
import org.kosa.enums.ProductCategory;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCardRes {

    private Long productId;
    private String name;
    private BigDecimal price;
    private BigDecimal discountValue;
    private ProductCategory category;

    private String farmName;
    // ✅ 대표 이미지 URL 추가
    private String imageUrl;

    public static ProductCardRes toProductCardRes(Product product) {
        return ProductCardRes.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .discountValue(product.getDiscountValue())
                .category(product.getCategory())
                .farmName(product.getFarmName())
                .build();
    }
}