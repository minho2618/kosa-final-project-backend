package org.kosa.controller;

import lombok.RequiredArgsConstructor;
import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionPhoto;
import org.kosa.service.ProductQuestionPhotoService;
import org.kosa.service.ProductQuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-question-photos")
@RequiredArgsConstructor
public class ProductQuestionPhotoController {

    private final ProductQuestionPhotoService productQuestionPhotoService;
    private final ProductQuestionService productQuestionService;

    @PostMapping("")
    public ResponseEntity<?> saveProductQuestionPhotos(@RequestBody List<ProductQuestionPhoto> photos) {
        productQuestionPhotoService.saveProductQuestionPhotos(photos);

        return ResponseEntity
                .status(201)
                .body("Save Product Question Photos");
    }

    /*@GetMapping("/{id}")
    public ResponseEntity<?> findByProductQuestionOrderBySortOrder(@PathVariable Long id) {
        productQuestionPhotoService.findByProductQuestionOrderBySortOrder(id);

        return ResponseEntity
                .status(201)
                .body("Save Product Question Photos");
    }*/

    /*@Transactional(readOnly = true)
    public ProductQuestionPhoto findByUrl(String url) {
        return productQuestionPhotoRepository.findByUrl(url);
    }*/


    /*@Transactional
    public void deleteByQuestionId(Long questionId) {
        // Assuming you will add `@Modifying @Query("DELETE FROM ProductQuestionPhoto p WHERE p.productQuestion.questionId = :questionId") void deleteByQuestionId(@Param("questionId") Long questionId);` to the repository
    }*/


    /*@PutMapping("/{id}")
    public int updateSortOrder(Long photoId, int sortOrder) {
        return productQuestionPhotoRepository.updateSortOrder(photoId, sortOrder);
    }*/

    /*@Transactional(readOnly = true)
    public long countByProductQuestion(ProductQuestion question) {
        // Assuming you will add `long countByProductQuestion(ProductQuestion question);` to the repository
        return 0; // Placeholder
    }*/


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductQuestionPhoto(@PathVariable Long id) {
        productQuestionService.deleteProductQuestion(id);

        return ResponseEntity
                .status(200)
                .body("Delete Complete: " + id);
    }
}
