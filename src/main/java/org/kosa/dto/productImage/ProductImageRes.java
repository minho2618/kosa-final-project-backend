package org.kosa.dto.productImage;

import lombok.*;
import org.kosa.entity.ProductImage;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRes {
    private Long imageId;
    private String url;
    private String altText;
    private Long productId;
    private int sortOrder;

    public static ProductImageRes toProductImageRes(ProductImage entity) {
        return ProductImageRes.builder()
                .imageId(entity.getImageId())
                .url(entity.getUrl())
                .altText(entity.getAltText())
                .productId(entity.getProduct().getProductId())
                .sortOrder(entity.getSortOrder())
                .build();
    }
}