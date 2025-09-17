package org.kosa.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kosa.dto.reviewPhoto.ReviewPhotoReq;
import org.kosa.dto.reviewPhoto.ReviewPhotoRes;
import org.kosa.service.ReviewPhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews/{reviewId}/photos")
@Validated
public class ReviewPhotoController {

    private final ReviewPhotoService reviewPhotoService;

    /** 리뷰의 사진 목록(정렬 순) */
    @GetMapping
    public List<ReviewPhotoRes> list(@PathVariable Long reviewId) {
        return reviewPhotoService.listByReview(reviewId);
    }

    /** 대표 사진(정렬 1순위) */
    @GetMapping("/cover")
    public ReviewPhotoRes cover(@PathVariable Long reviewId) {
        return reviewPhotoService.getCover(reviewId);
    }

    /** 단건 추가 */
    @PostMapping
    public ResponseEntity<ReviewPhotoRes> add(@PathVariable Long reviewId,
                                              @RequestBody @Valid ReviewPhotoReq req) {
        ReviewPhotoRes res = reviewPhotoService.add(reviewId, req);
        return ResponseEntity
                .created(URI.create(String.format("/api/reviews/%d/photos/%d", reviewId, res.getPhotoId())))
                .body(res);
    }

    /** 전체 교체(기존 삭제 후 새 목록으로) */
    @PostMapping("/replace")
    public List<ReviewPhotoRes> replaceAll(@PathVariable Long reviewId,
                                           @RequestBody List<@Valid ReviewPhotoReq> reqs) {
        return reviewPhotoService.replaceAll(reviewId, reqs);
    }

    /** 단건 수정 (URL/정렬) */
    @PutMapping("/{photoId}")
    public ReviewPhotoRes update(@PathVariable Long reviewId,
                                 @PathVariable Long photoId,
                                 @RequestBody @Valid ReviewPhotoReq req) {
        // reviewId는 경로 일치 확인 용도로만 쓰려면 서비스에 검증 로직 추가 가능
        return reviewPhotoService.update(photoId, req);
    }

    /** 단건 삭제 */
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId,
                                       @PathVariable Long photoId) {
        reviewPhotoService.delete(photoId);
        return ResponseEntity.noContent().build();
    }
}
