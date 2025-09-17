package org.kosa.dto.review;

import lombok.*;
import org.kosa.dto.reviewPhoto.ReviewPhotoReq;
import org.kosa.dto.reviewPhoto.ReviewPhotoRes;
import org.kosa.entity.Review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRes {

    private Long reviewId;
    private Long rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 연관된 엔티티의 ID만 포함하거나, 별도 DTO로 변환
    private Long productId;
    private Long memberId;
    private String memberName; // 예시로 추가


    // Entity -> DTO 변환을 위한 정적 팩토리 메소드
    public static ReviewRes toReviewRes(Review entity) {
        return ReviewRes.builder()
                .reviewId(entity.getReviewId())
                .rating(entity.getRating())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .productId(entity.getProduct().getProductId())
                .memberId(entity.getMember().getMemberId())
                .memberName(entity.getMember().getName())
                .build();
    }

}