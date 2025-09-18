package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kosa.entity.ProductQuestionPhoto;
import org.kosa.service.ProductQuestionPhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ProductQuestionPhoto", description = "상품 문의 사진 API")
@RestController
@RequestMapping("/api/product-question-photos")
@RequiredArgsConstructor
public class ProductQuestionPhotoController {

    private final ProductQuestionPhotoService productQuestionPhotoService;

    @Operation(summary = "상품 문의 사진 목록 저장", description = "상품 문의에 첨부될 사진 목록을 한 번에 저장합니다.")
    @ApiResponse(responseCode = "201", description = "저장 성공")
    @PostMapping("")
    public ResponseEntity<?> saveProductQuestionPhotos(@RequestBody List<ProductQuestionPhoto> photos) {
        productQuestionPhotoService.saveProductQuestionPhotos(photos);

        return ResponseEntity
                .status(201)
                .body("Save Product Question Photos");
    }

    @Operation(summary = "상품 문의 사진 삭제", description = "특정 상품 문의 사진을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductQuestionPhoto(
            @Parameter(description = "삭제할 사진의 ID", required = true) @PathVariable Long id) {
        productQuestionPhotoService.deleteProductQuestionPhoto(id);

        return ResponseEntity
                .status(200)
                .body("Delete Complete: " + id);
    }
}