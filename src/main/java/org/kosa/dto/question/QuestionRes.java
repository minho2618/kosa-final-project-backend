package org.kosa.dto.question;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Question;
import org.kosa.enums.QuestionStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Builder
public class QuestionRes {
    private Long questionId;
    private String title;
    private String content;
    private QuestionStatus status;
    private LocalDateTime createdAt;
    private Long memberId;
    private String authorName;

    public QuestionRes toQuestionRes(Question question) {
        return QuestionRes.builder()
                .questionId(question.getQuestionId())
                .title(question.getTitle())
                .content(question.getContent())
                .status(question.getStatus())
                .createdAt(question.getCreatedAt())
                .memberId(question.getMember().getMemberId())
                .authorName(question.getMember().getName())
                .build();
    }
}
