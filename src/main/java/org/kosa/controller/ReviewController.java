package org.kosa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.review.ReviewReq;
import org.kosa.dto.review.ReviewRes;
import org.kosa.entity.Review;
import org.kosa.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Review", description = "리뷰 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 생성", description = "상품에 대한 리뷰를 사진 URL과 함께 생성합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @PostMapping("")
    public ResponseEntity<ReviewRes> create(@RequestBody @Valid ReviewReq req) {
        Review saved = reviewService.create(req);
        ReviewRes body = ReviewRes.toReviewRes(saved);
        return ResponseEntity
                .created(URI.create("/api/reviews/" + body.getReviewId()))
                .body(body);
    }

    @Operation(summary = "리뷰 단건 조회", description = "리뷰 ID로 특정 리뷰를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{reviewId}")
    public ReviewRes get(@Parameter(description = "조회할 리뷰의 ID", required = true) @PathVariable Long reviewId) {
        Review review = reviewService.get(reviewId);
        return ReviewRes.toReviewRes(review);
    }

    @Operation(summary = "상품별 리뷰 목록 조회", description = "특정 상품에 달린 모든 리뷰 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{productId}/reviews")
    public List<ReviewRes> listByProduct(@Parameter(description = "리뷰 목록을 조회할 상품의 ID", required = true) @PathVariable Long productId) {
        return reviewService.listByProduct(productId).stream()
                .map(ReviewRes::toReviewRes)
                .toList();
    }

    @Operation(summary = "리뷰 수정", description = "기존 리뷰의 평점, 내용, 사진 목록을 수정(교체)합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PatchMapping("/{reviewId}")
    public ReviewRes update(
            @Parameter(description = "수정할 리뷰의 ID", required = true) @PathVariable Long reviewId,
            @RequestBody ReviewReq req) {
        Review updated = reviewService.update(reviewId, req);
        return ReviewRes.toReviewRes(updated);
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰와 관련된 사진을 함께 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@Parameter(description = "삭제할 리뷰의 ID", required = true) @PathVariable Long reviewId) {
        reviewService.delete(reviewId);
        return ResponseEntity.noContent().build();
    }
}