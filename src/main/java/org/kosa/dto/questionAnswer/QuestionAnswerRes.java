package org.kosa.dto.questionAnswer;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Question;
import org.kosa.entity.QuestionAnswer;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Builder
public class QuestionAnswerRes {
    private Long AnswerId;
    private Long QuestionId;
    private String content;
    private LocalDateTime createdAt;

    public QuestionAnswerRes toQuestionAnswerRes(QuestionAnswer questionAnswer){
        return QuestionAnswerRes.builder()
                .AnswerId(questionAnswer.getAnswerId())
                .QuestionId(questionAnswer.getQuestion().getQuestionId())
                .content(questionAnswer.getContent())
                .createdAt(questionAnswer.getCreatedAt())
                .build();
    }
}
