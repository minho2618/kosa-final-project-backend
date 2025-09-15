package org.kosa.dto.product;

import lombok.*;
import org.kosa.entity.Product;
import org.kosa.entity.ProductImage;
import org.kosa.enums.ProductCategory;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListRes {

    private Long productId;
    private String name;
    private BigDecimal price;
    private BigDecimal discountValue;
    private ProductCategory category;
    private String thumbnailUrl; // 대표 이미지 URL

    public static ProductListRes from(Product product, ProductImage thumbnail) {
        return ProductListRes.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .discountValue(product.getDiscountValue())
                .category(product.getCategory())
                .thumbnailUrl(thumbnail != null ? thumbnail.getUrl() : null)
                .build();
    }
}