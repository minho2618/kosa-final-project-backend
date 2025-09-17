package org.kosa.dto.question;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.kosa.entity.Question;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Slf4j
@Builder
public class QuestionReq {
    private String title;
    private String content;

    public static Question toQuestion(QuestionReq req){
        return Question.builder()
                .title(req.getTitle())
                .content(req.getContent())
                .build();
    }
}
