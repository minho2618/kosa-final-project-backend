package org.kosa.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
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
