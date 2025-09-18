package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.productQuestion.ProductQuestionReq;
import org.kosa.dto.productQuestion.ProductQuestionRes;
import org.kosa.service.ProductQuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ProductQuestion", description = "상품 문의 API")
@RestController
@RequestMapping("/api/product-questions")
@RequiredArgsConstructor
public class ProductQuestionController {

    private final ProductQuestionService productQuestionService;

    @Operation(summary = "상품 문의 생성", description = "특정 상품에 대한 문의를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공, 생성된 문의 ID 반환")
    @PostMapping("")
    public ResponseEntity<?> createProductQuestion(@RequestBody ProductQuestionReq productQuestionReq) {
        Long productQuestionId = productQuestionService.createProductQuestion(productQuestionReq);

        return ResponseEntity
                .status(201)
                .body(productQuestionId);
    }

    @Operation(summary = "상품별 문의 목록 조회", description = "특정 상품에 달린 모든 문의 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{productId}")
    public ResponseEntity<?> findByProduct(
            @Parameter(description = "문의 목록을 조회할 상품의 ID", required = true) @PathVariable Long productId) {
        List<ProductQuestionRes> resList = productQuestionService.findByProduct(productId);

        return ResponseEntity
                .status(200)
                .body(resList);
    }

    @Operation(summary = "회원별 문의 목록 조회", description = "특정 회원이 작성한 모든 상품 문의 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/member/{id}")
    public ResponseEntity<?> findByMember(
            @Parameter(description = "문의 목록을 조회할 회원의 ID", required = true) @PathVariable Long id) {
        List<ProductQuestionRes> resList = productQuestionService.findByMember(id);

        return ResponseEntity
                .status(200)
                .body(resList);
    }

    @Operation(summary = "상품 문의 상세 조회", description = "문의 ID로 특정 상품 문의의 상세 내용을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<?> findByIdWithDetails(
            @Parameter(description = "상세 조회할 문의의 ID", required = true) @PathVariable Long id) {
        ProductQuestionRes productQuestionRes = productQuestionService.findByIdWithDetails(id);

        return ResponseEntity
                .status(200)
                .body(productQuestionRes);
    }

    @Operation(summary = "상품 문의 수정", description = "기존 상품 문의 내용을 수정합니다.")
    @ApiResponse(responseCode = "201", description = "수정 성공")
    @PutMapping("/{questionId}")
    public ResponseEntity<?> updateProductQuestion(
            @Parameter(description = "수정할 문의의 ID", required = true) @PathVariable Long questionId,
            @RequestBody ProductQuestionReq productQuestionReq) {
        productQuestionService.updateProductQuestion(questionId, productQuestionReq);

        return ResponseEntity
                .status(201)
                .body(productQuestionReq);
    }

    @Operation(summary = "상품 문의 삭제", description = "상품 문의와 관련된 답변, 사진을 모두 함께 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{questionId}")
    public ResponseEntity<?> deleteProductQuestion(
            @Parameter(description = "삭제할 문의의 ID", required = true) @PathVariable Long questionId) {
        productQuestionService.deleteProductQuestion(questionId);

        return ResponseEntity
                .status(200)
                .body("Delete Complete: " + questionId);
    }
}