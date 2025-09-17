package org.kosa.dto.questionAnswer;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Question;
import org.kosa.entity.QuestionAnswer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Builder
public class QuestionAnswerReq {
    private String content;
    private Long questionId;

    public static QuestionAnswer toQuestionAnswer(QuestionAnswerReq req,  Question question) {
        return QuestionAnswer.builder()
                .content(req.getContent())
                .question(question)
                .build();
    }
}
