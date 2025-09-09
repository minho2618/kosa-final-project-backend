package org.kosa.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
            (name="user_id")
    private User user;

    @Override
    public String toString() {
        return "Reviews{" +
                "reviewId=" + reviewId +
                ", rating=" + rating +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", products=" + product +
                ", user=" + user +
                '}';
    }
}
