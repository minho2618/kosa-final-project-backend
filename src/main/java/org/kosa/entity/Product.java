package org.kosa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.kosa.enums.ProductCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Product {
    @Id @Column(name= "product_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
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
            (name = "seller_user_id")
    private Seller seller;


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
                ", seller=" + seller +
                '}';
    }
}