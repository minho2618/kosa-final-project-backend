package org.kosa.dto.recipe;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IngredientDto {
    private String name;
    private String qty;
    private String productId;
}
