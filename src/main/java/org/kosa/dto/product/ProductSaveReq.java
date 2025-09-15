package org.kosa.dto.product;

import lombok.Getter;
import lombok.Setter;
import org.kosa.entity.Product;
import org.kosa.entity.Seller;
import org.kosa.enums.ProductCategory;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductSaveReq {

    private String name;
    private String description;
    private BigDecimal price;
    private ProductCategory category;
    private BigDecimal discountValue;
    private Boolean isActive;
    private List<ProductImageReq> images;

    // 이미지 정보를 담기 위한 내부 DTO
    @Getter
    @Setter
    public static class ProductImageReq {
        private String url;
        private String altText;
        private int sortOrder;
    }

    public Product toEntity(Seller seller) {
        return Product.builder()
                .name(this.name)
                .description(this.description)
                .price(this.price)
                .category(this.category)
                .discountValue(this.discountValue)
                .isActive(this.isActive)
                .seller(seller)
                .build();
    }
}