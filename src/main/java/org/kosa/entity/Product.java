package org.kosa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.kosa.dto.productImage.ProductImageRes;
import org.kosa.enums.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(length = 150)
    private String name;

    @Lob
    private String description;

    private BigDecimal price;

    private ProductCategory category;

    private BigDecimal discountValue;

    private Boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
            (fetch = FetchType.LAZY)
    @JoinColumn
            (name = "seller_member_id")
    private Seller seller;

    @OneToMany
            (fetch = FetchType.LAZY)
    @JoinColumn
            (name = "product_id")
    @Cascade(CascadeType.REMOVE)
    private List<ProductImageRes> images; // 전체 이미지 목록


    @Override
    public String toString() {
        return "Products{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", discountValue=" + discountValue +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}