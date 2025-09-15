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

    public Question toQuestion(){
        return Question.builder()
                .title(title)
                .content(content)
                .build();
    }
}
