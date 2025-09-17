package org.kosa.dto.product;

import lombok.*;
import org.kosa.dto.productImage.ProductImageReq;
import org.kosa.entity.Product;
import org.kosa.entity.Seller;
import org.kosa.enums.ProductCategory;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReq {

    private String name;
    private String description;
    private BigDecimal price;
    private ProductCategory category;
    private BigDecimal discountValue;
    private Boolean isActive;

    public static Product toProduct(ProductReq req, Seller seller) {
        return Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .category(req.getCategory())
                .discountValue(req.getDiscountValue())
                .isActive(req.getIsActive())
                .seller(seller)
                .build();
    }
}