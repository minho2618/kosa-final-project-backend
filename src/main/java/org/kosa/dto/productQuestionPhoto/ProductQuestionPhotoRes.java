package org.kosa.dto.productQuestionPhoto;

import jakarta.persistence.*;
import lombok.*;
import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionPhoto;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProductQuestionPhotoRes {
    private Long photoId;
    private String url;
    private int sortOrder;
    private Long productQuestionId;

    public ProductQuestionPhotoRes toProductQuestionPhotoRes(ProductQuestionPhoto entity) {
        return ProductQuestionPhotoRes.builder()
                .photoId(entity.getPhotoId())
                .url(entity.getUrl())
                .sortOrder(entity.getSortOrder())
                .productQuestionId(entity.getProductQuestion().getQuestionId())
                .build();
    }
}
