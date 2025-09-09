package org.kosa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Slf4j
public class ProductQuestionAnswer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Lob
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private ProductQuestion productQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_id")
    private User users;

    @Override
    public String toString() {
        return "ProductQuestionAnswer{" +
                "answerId=" + answerId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
