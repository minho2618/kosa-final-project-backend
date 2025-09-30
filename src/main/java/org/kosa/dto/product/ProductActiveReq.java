package org.kosa.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "상품 활성 상태 요청 DTO")
@Getter
@Setter
public class ProductActiveReq {
    @Schema(description = "활성화 여부", example = "true")
    private boolean active;
}