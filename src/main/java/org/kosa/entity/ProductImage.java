package org.kosa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;
    @Column(length = 512)
    private String url;
    @Column(length = 200)
    private String altText;
    private int sortOrder;

    @ManyToOne
            (fetch = FetchType.LAZY)
    @JoinColumn
            (name="product_id")
    @JsonIgnore
    private Product product;

    @Override
    public String toString() {
        return "Product_images{" +
                "imageId=" + imageId +
                ", url=" + url +
                ", altText='" + altText + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
