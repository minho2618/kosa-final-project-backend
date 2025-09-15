package org.kosa.dto.productQuestionPhoto;

import lombok.*;
import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionPhoto;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProductQuestionPhotoReq {
    private String url;
    private int sortOrder;
    private Long productQuestionId;

    public ProductQuestionPhoto toProductQuestionPhoto(ProductQuestionPhotoReq req) {
        return ProductQuestionPhoto.builder()
                .url(req.getUrl())
                .sortOrder(req.getSortOrder())
                .productQuestion(ProductQuestion.builder().questionId(req.getProductQuestionId()).build())
                .build();
    }
}
