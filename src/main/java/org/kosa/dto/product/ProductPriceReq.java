package org.kosa.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Schema(description = "상품 가격 변경 요청 DTO")
@Getter
@Setter
public class ProductPriceReq {
    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "가격은 0 이상이어야 합니다.")
    @Schema(description = "새로운 가격", example = "15000.00")
    private BigDecimal price;
}