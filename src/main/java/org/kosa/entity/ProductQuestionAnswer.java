package org.kosa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Slf4j
public class ProductQuestionAnswer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Lob
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "product_question_id", unique = true)
    private Long productQuestionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_id")
    private Users users;

    @Override
    public String toString() {
        return "ProductQuestionAnswer{" +
                "answerId=" + answerId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
