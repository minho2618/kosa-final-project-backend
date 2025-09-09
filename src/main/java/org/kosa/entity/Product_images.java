package org.kosa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Product_images {
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
    private Products products;

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
