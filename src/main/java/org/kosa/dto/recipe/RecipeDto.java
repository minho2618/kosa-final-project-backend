package org.kosa.dto.recipe;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecipeDto {
    private String id;
    private String title;
    private String thumbnail;
    private List<String> tags;
    private Integer timeMinutes;
    private String difficulty;            // "쉬움" | "보통" | "어려움"
    private List<IngredientDto> ingredients;
    private List<String> steps;
}
