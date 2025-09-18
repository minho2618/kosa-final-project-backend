package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.productQuestionAnswer.ProductQuestionAnswerReq;
import org.kosa.dto.productQuestionAnswer.ProductQuestionAnswerRes;
import org.kosa.service.ProductQuestionAnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ProductQuestionAnswer", description = "상품 문의 답변 API")
@RestController
@RequestMapping("/api/product-question-answers")
@RequiredArgsConstructor
public class ProductQuestionAnswerController {

    private final ProductQuestionAnswerService productQuestionAnswerService;

    @Operation(summary = "상품 문의 답변 생성", description = "특정 상품 문의에 대한 답변을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공, 생성된 답변 ID 반환")
    @PostMapping("")
    public ResponseEntity<?> createProductQuestionAnswer(@RequestBody ProductQuestionAnswerReq req) {
        Long productQuestionAnswerId = productQuestionAnswerService.createProductQuestionAnswer(req);

        return ResponseEntity
                .status(201)
                .body(productQuestionAnswerId);
    }

    @Operation(summary = "상품 문의에 대한 답변 조회", description = "상품 문의 ID를 사용하여 해당하는 답변을 조회합니다.")
    @ApiResponse(responseCode = "202", description = "조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<?> findByProductQuestion(
            @Parameter(description = "답변을 조회할 상품 문의의 ID", required = true) @PathVariable Long id) {
        ProductQuestionAnswerRes res = productQuestionAnswerService.findByProductQuestionId(id);

        return ResponseEntity
                .status(202)
                .body(res);
    }
}