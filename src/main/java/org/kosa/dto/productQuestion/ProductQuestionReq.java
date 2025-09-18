package org.kosa.dto.productQuestion;

import lombok.*;
import org.kosa.dto.productQuestionPhoto.ProductQuestionPhotoReq;
import org.kosa.entity.Member;
import org.kosa.entity.Product;
import org.kosa.entity.ProductQuestion;
import org.kosa.enums.ProductQuestionsStatus;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ProductQuestionReq {
    private Long productId;
    private String content;
    private ProductQuestionsStatus status;
    private Long memberId;
    private List<ProductQuestionPhotoReq> productQuestionPhotoReqList;

    public static ProductQuestion toProductQuestion(ProductQuestionReq req) {
        return ProductQuestion.builder()
                .product(Product.builder().productId(req.getProductId()).build())
                .content(req.getContent())
                .status(req.getStatus())
                .member(Member.builder().memberId(req.getMemberId()).build())
                .build();
    }

    public ProductQuestion toEntity() {
        return ProductQuestion.builder()
                .product(Product.builder().productId(this.getProductId()).build())
                .content(this.getContent())
                .status(this.getStatus())
                .member(Member.builder().memberId(this.getMemberId()).build())
                .build();
    }
}
