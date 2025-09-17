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
public class ProductQuestionAnswerRes {
    private Long answerId;
    private String content;
    private LocalDateTime createdAt;
    private Long productQuestionId;
    private Long memberId;

    public static ProductQuestionAnswerRes toProductQuestionAnswerRes(ProductQuestionAnswer entity) {
        return ProductQuestionAnswerRes.builder()
                .answerId(entity.getAnswerId())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .productQuestionId(entity.getProductQuestionId())
                .memberId(entity.getMember().getMemberId())
                .build();
    }
}
