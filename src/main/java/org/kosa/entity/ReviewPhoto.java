package org.kosa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReviewPhoto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long photoId;
    @Column(length = 512)
    private String url;
    private int sortOrder;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
            (fetch = FetchType.LAZY)
    @JoinColumn
            (name="reivew_id")
    private Review review;

    @Override
    public String toString() {
        return "Review_photos{" +
                "photoId=" + photoId +
                ", url='" + url + '\'' +
                ", sortOrder=" + sortOrder +
                ", createdAt=" + createdAt +
                ", reviews=" + review +
                '}';
    }
}
