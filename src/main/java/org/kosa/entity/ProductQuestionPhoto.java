package org.kosa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Slf4j
@Entity
public class ProductQuestionPhoto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;

    @Column(length = 512)
    private String url;

    private int sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pq_id")
    private ProductQuestion productQuestion;

    @Override
    public String toString() {
        return "ProductQuestionPhoto{" +
                "photoId=" + photoId +
                ", url='" + url + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
