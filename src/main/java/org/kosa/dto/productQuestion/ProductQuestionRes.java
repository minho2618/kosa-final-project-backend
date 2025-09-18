package org.kosa.dto.productQuestion;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.kosa.dto.productQuestionPhoto.ProductQuestionPhotoRes;
import org.kosa.entity.Member;
import org.kosa.entity.Product;
import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionPhoto;
import org.kosa.enums.ProductQuestionsStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProductQuestionRes {
    private Long questionId;
    private Long productId;
    private String content;
    private ProductQuestionsStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long memberId;
    private List<ProductQuestionPhotoRes> productQuestionPhotoResList;

    public static ProductQuestionRes toProductQuestionRes(ProductQuestion entity) {
        return ProductQuestionRes.builder()
                .questionId(entity.getQuestionId())
                .productId(entity.getProduct().getProductId())
                .content(entity.getContent())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .memberId(entity.getMember().getMemberId())
                .build();
    }
}
