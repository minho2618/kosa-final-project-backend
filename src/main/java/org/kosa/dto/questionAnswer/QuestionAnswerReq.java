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

    public QuestionAnswer toQuestionAnswer(Question question) {
        return QuestionAnswer.builder()
                .content(content)
                .question(question)
                .build();
    }
}
