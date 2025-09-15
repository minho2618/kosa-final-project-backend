package org.kosa.dto.productQuestion;

import lombok.*;
import org.kosa.dto.productQuestionPhoto.ProductQuestionPhotoReq;
import org.kosa.entity.Member;
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
    private String content;
    private ProductQuestionsStatus status;
    private Long memberId;
    private List<ProductQuestionPhotoReq> productQuestionPhotoReqList;

    public ProductQuestion toProductQuestion(ProductQuestionReq req) {
        return ProductQuestion.builder()
                .content(req.getContent())
                .status(req.getStatus())
                .member(Member.builder().memberId(req.getMemberId()).build())
                .productQuestionPhotoList(req.getProductQuestionPhotoReqList()
                        .stream()
                        .map((photo) -> new ProductQuestionPhotoReq().toProductQuestionPhoto(photo)).collect(Collectors.toList()))
                .build();
    }
}
