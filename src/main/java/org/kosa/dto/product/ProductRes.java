package org.kosa.dto.product;

import lombok.*;
import org.kosa.dto.productImage.ProductImageRes;
import org.kosa.dto.seller.SellerRes;
import org.kosa.entity.Product;
import org.kosa.entity.ProductImage;
import org.kosa.entity.Seller;
import org.kosa.enums.ProductCategory;
import org.kosa.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRes {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private ProductCategory category;
    private BigDecimal discountValue;
    private String status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 엔티티 직접 노출 X
    private SellerRes seller;


    public static ProductRes toProductRes(Product product) {
        return ProductRes.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .discountValue(product.getDiscountValue())
                .isActive(product.getIsActive())
                .status(product.getStatus().name())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .seller(product.getSeller() !=null ? SellerRes.toSellerRes(product.getSeller()) : null)
                .build();
    }
}

