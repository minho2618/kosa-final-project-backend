package org.kosa.dto.review;

import lombok.*;
import org.kosa.entity.Member;
import org.kosa.entity.Product;
import org.kosa.entity.Review;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewReq {

    private Long productId;
    private Long memberId;
    private Long rating;
    private String content;
    private List<String> photoUrls; // 사진 URL들을 리스트로 받음

    // DTO를 Review 엔티티로 변환하는 메소드
    public static Review toReview(Review rev, Product product, Member member) {
        return Review.builder()
                .product(product)
                .member(member)
                .rating(rev.getRating())
                .content(rev.getContent())
                .createdAt(LocalDateTime.now())
                .build();
    }
}