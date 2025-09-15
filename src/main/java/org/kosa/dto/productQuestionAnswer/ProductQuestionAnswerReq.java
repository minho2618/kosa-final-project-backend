package org.kosa.dto.productQuestionAnswer;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.kosa.entity.Member;
import org.kosa.entity.ProductQuestionAnswer;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProductQuestionAnswerReq {
    private String content;
    private Long productQuestionId;
    private Long memberId;

    public ProductQuestionAnswer toProductQuestionAnswer(ProductQuestionAnswerReq req) {
        return ProductQuestionAnswer.builder()
                .content(req.getContent())
                .productQuestionId(req.getProductQuestionId())
                .member(Member.builder().memberId(req.getProductQuestionId()).build())
                .build();
    }
}
