package org.kosa.dto.reviewPhoto;

import lombok.*;
import org.kosa.entity.ReviewPhoto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPhotoRes {

    private Long photoId;
    private String url;
    private int sortOrder;
    private LocalDateTime createdAt;

    // Entity -> DTO 변환을 위한 정적 팩토리 메소드
    public static ReviewPhotoRes toReviewPhotoRes(ReviewPhoto entity) {
        return ReviewPhotoRes.builder()
                .photoId(entity.getPhotoId())
                .url(entity.getUrl())
                .sortOrder(entity.getSortOrder())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

