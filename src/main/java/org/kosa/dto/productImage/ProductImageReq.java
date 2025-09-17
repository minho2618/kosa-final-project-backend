package org.kosa.dto.productImage;

import lombok.*;
import org.kosa.entity.Product;
import org.kosa.entity.ProductImage;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductImageReq {
    private Long productId;
    private String url;
    private String altText;
    private int sortOrder;

    public static ProductImage toProductImage(ProductImageReq req) {
        return ProductImage.builder()
                .url(req.getUrl())
                .altText(req.getAltText())
                .sortOrder(req.getSortOrder())
                .product(Product.builder().productId(req.getProductId()).build())
                .build();
    }


}
