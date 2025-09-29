package org.kosa.controller;


import lombok.RequiredArgsConstructor;
import org.kosa.dto.recipe.RecipeListResponse;
import org.kosa.dto.recipe.RecipeRequest;
import org.kosa.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    @PostMapping("/recipe")
    public ResponseEntity<RecipeListResponse> getRecipes(@RequestBody RecipeRequest req) {
        RecipeListResponse result = geminiService.generateRecipes(req.getIngredients());
        return ResponseEntity.ok(result);
    }
}
