package org.kosa.dto.recipe;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeListResponse {
    private List<RecipeDto> recipes;
}