package org.kosa.dto.reviewPhoto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPhotoReq {

    /** 이미지 URL (필수) */
    @Size(max = 512, message = "이미지 URL은 512자를 초과할 수 없습니다.")
    private String url;

    @Min(value = 1, message = "정렬 순서는 1 이상이어야 합니다.")
    private Integer sortOrder;
}
