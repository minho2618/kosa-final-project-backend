package org.kosa.controller;

import lombok.RequiredArgsConstructor;
import org.kosa.dto.product.ProductReq;
import org.kosa.dto.productQuestion.ProductQuestionReq;
import org.kosa.dto.productQuestion.ProductQuestionRes;
import org.kosa.entity.*;
import org.kosa.enums.ProductQuestionsStatus;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.ProductQuestionRepository;
import org.kosa.service.ProductQuestionAnswerService;
import org.kosa.service.ProductQuestionPhotoService;
import org.kosa.service.ProductQuestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/product-questions")
@RequiredArgsConstructor
public class ProductQuestionController {

    private final ProductQuestionService productQuestionService;
    private final ProductQuestionPhotoService productQuestionPhotoService;
    private final ProductQuestionAnswerService productQuestionAnswerService;


    @PostMapping("")
    public ResponseEntity<?> createProductQuestion(ProductQuestionReq productQuestionReq) {
        Long productQuestionId = productQuestionService.createProductQuestion(productQuestionReq);

        return ResponseEntity
                .status(201)
                .body(productQuestionId);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> findByProduct(@PathVariable Long productId) {
        List<ProductQuestionRes> resList = productQuestionService.findByProduct(productId);

        return ResponseEntity
                .status(200)
                .body(resList);
    }

    // ToDo: 페이지네이션 적용할 것
    /*@GetMapping("/")
    public ResponseEntity<?> findByProduct(Product product, Pageable pageable) {
        return productQuestionRepository.findByProduct(product, pageable);
    }*/

    @GetMapping("/member/{id}")
    public ResponseEntity<?> findByMember(@PathVariable Long id) {
        List<ProductQuestionRes> resList = productQuestionService.findByMember(id);

        return ResponseEntity
                .status(200)
                .body(resList);
    }


    /*@Transactional(readOnly = true)
    public ResponseEntity<?> findByStatus(ProductQuestionsStatus status) {
        return productQuestionRepository.findByStatus(status);
    }*/

    /*@Transactional(readOnly = true)
    public ResponseEntity<?> findByUpdatedAtAfter(LocalDateTime date) {
        return productQuestionRepository.findByUpdatedAtAfter(date);
    }*/

    /*@Transactional(readOnly = true)
    public ResponseEntity<?> findPendingQuestions(Product product, ProductQuestionsStatus status) {
        return productQuestionRepository.findPendingQuestions(product, status);
    }*/

    /*@PutMapping("/status/{id}")
    public ResponseEntity<?> updateStatusByIds(List<Long> ids, ProductQuestionsStatus status) {
        productQuestionService.updateStatusByIds()

        return productQuestionRepository.updateStatusByIds(ids, status);
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdWithDetails(@PathVariable Long id) {
        ProductQuestionRes productQuestionRes = productQuestionService.findByIdWithDetails(id);

        return ResponseEntity
                .status(200)
                .body(productQuestionRes);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<?> updateProductQuestion(@PathVariable Long questionId, @RequestBody ProductQuestionReq productQuestionReq) {
        productQuestionService.updateProductQuestion(questionId, productQuestionReq);

        return ResponseEntity
                .status(201)
                .body(productQuestionReq);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<?> deleteProductQuestion(@PathVariable Long questionId) {
        productQuestionService.deleteProductQuestion(questionId);

        return ResponseEntity
                .status(200)
                .body("Delete Complete: " + questionId);
    }
}
