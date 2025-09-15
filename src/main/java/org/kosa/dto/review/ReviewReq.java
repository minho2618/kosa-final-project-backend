package org.kosa.dto.review;

import lombok.Getter;
import lombok.Setter;
import org.kosa.entity.Member;
import org.kosa.entity.Product;
import org.kosa.entity.Review;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReviewReq {

    private Long productId;
    private Long memberId;
    private Long rating;
    private String content;
    private List<String> photoUrls; // 사진 URL들을 리스트로 받음

    // DTO를 Review 엔티티로 변환하는 메소드
    public Review toEntity(Product product, Member member) {
        return Review.builder()
                .product(product)
                .member(member)
                .rating(this.rating)
                .content(this.content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}