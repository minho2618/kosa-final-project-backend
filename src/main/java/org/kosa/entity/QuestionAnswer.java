package org.kosa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class QuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", unique = true)
    private Question question;

    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "QuestionAnswer{" +
                "answerId=" + answerId +
                ", question=" + question +
                ", content='" + content + '\'' +
                '}';
    }
}
