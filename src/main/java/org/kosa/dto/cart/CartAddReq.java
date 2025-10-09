package org.kosa.dto.cart;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Slf4j
@Builder
public class CartAddReq {
    private Long productId;
    private int quantity;
    private String name;
    private BigDecimal price;
    private String farm;
    private String imageUrl;
}
