package org.kosa.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.kosa.dto.reviewPhoto.ReviewPhotoRes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private Long rating;

    @Lob
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne
            (fetch = FetchType.LAZY)
    @JoinColumn
            (name ="product_id")
    private Product product;

    @ManyToOne
            (fetch = FetchType.LAZY)
    @JoinColumn
            (name="member_id")
    private Member member;

    @OneToMany
            (fetch = FetchType.LAZY)
    @JoinColumn
            (name="review_id")
    @Cascade(CascadeType.REMOVE)
    private List<ReviewPhotoRes> photos;



    @Override
    public String toString() {
        return "Reviews{" +
                "reviewId=" + reviewId +
                ", rating=" + rating +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", products=" + product +
                '}';
    }
}
