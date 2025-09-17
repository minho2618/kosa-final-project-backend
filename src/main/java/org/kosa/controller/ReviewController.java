package org.kosa.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    /** 리뷰 생성 */
    @PostMapping("")
    public ResponseEntity<ReviewRes> create(@RequestBody @Valid ReviewReq req) {
        Review saved = reviewService.create(req);
        ReviewRes body = ReviewRes.toReviewRes(saved);
        return ResponseEntity
                .created(URI.create("/api/reviews/" + body.getReviewId()))
                .body(body);
    }

    /** 리뷰 단건 조회 */
    @GetMapping("/{reviewId}")
    public ReviewRes get(@PathVariable Long reviewId) {
        Review review = reviewService.get(reviewId);
        return ReviewRes.toReviewRes(review);
    }

    /** 상품별 리뷰 목록 */
    @GetMapping("/{productId}/reviews")
    public List<ReviewRes> listByProduct(@PathVariable Long productId) {
        return reviewService.listByProduct(productId).stream()
                .map(ReviewRes::toReviewRes)
                .toList();
    }

    /** 리뷰 부분 수정 (rating/content, photoUrls 교체 포함) */
    @PatchMapping("/{reviewId}")
    public ReviewRes update(@PathVariable Long reviewId, @RequestBody ReviewReq req) {
        Review updated = reviewService.update(reviewId, req);
        return ReviewRes.toReviewRes(updated);
    }

    /** 리뷰 삭제 */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId) {
        reviewService.delete(reviewId);
        return ResponseEntity.noContent().build();
    }
}
