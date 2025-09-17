package org.kosa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.dto.reviewPhoto.ReviewPhotoReq;
import org.kosa.dto.reviewPhoto.ReviewPhotoRes;
import org.kosa.entity.Review;
import org.kosa.entity.ReviewPhoto;
import org.kosa.exception.RecordNotFoundException;
import org.kosa.repository.ReviewPhotoRepository;
import org.kosa.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewPhotoService {

    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    // ========= 조회 =========

    @Transactional(readOnly = true)
    public List<ReviewPhotoRes> listByReview(Long reviewId) {
        ensureReview(reviewId);
        return reviewPhotoRepository.findByReview_ReviewIdOrderBySortOrderAsc(reviewId)
                .stream()
                .map(ReviewPhotoRes::toReviewPhotoRes)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewPhotoRes getCover(Long reviewId) {
        ensureReview(reviewId);
        ReviewPhoto cover = reviewPhotoRepository
                .findFirstByReview_ReviewIdOrderBySortOrderAsc(reviewId)
                .orElseThrow(() -> new RecordNotFoundException("대표 이미지 없습니다", "Not Found reviewPhoto"));
        return ReviewPhotoRes.toReviewPhotoRes(cover);
    }

    // ========= 생성 =========

    @Transactional
    public ReviewPhotoRes add(Long reviewId, ReviewPhotoReq req) {
        Review review = ensureReview(reviewId);

        int nextSort = reviewPhotoRepository.findMaxSortOrder(reviewId) + 1;
        int finalSort = (req.getSortOrder() == null || req.getSortOrder() <= 0) ? nextSort : req.getSortOrder();

        ReviewPhoto photo = ReviewPhoto.builder()
                .url(req.getUrl())
                .sortOrder(finalSort)
                .review(review)
                .build();

        ReviewPhoto saved = reviewPhotoRepository.save(photo);
        // 중간 끼워넣기라면 보정
        normalizeSort(reviewId);

        log.info("리뷰 사진 등록: reviewId={}, photoId={}, sort={}", reviewId, saved.getPhotoId(), saved.getSortOrder());
        return ReviewPhotoRes.toReviewPhotoRes(saved);
    }

    /** 기존 사진 전체 삭제 후 새 목록으로 교체 */
    @Transactional
    public List<ReviewPhotoRes> replaceAll(Long reviewId, List<ReviewPhotoReq> reqs) {
        Review review = ensureReview(reviewId);

        reviewPhotoRepository.deleteByReview_ReviewId(reviewId);

        List<ReviewPhoto> batch = new ArrayList<>();
        int defaultSort = 1;
        for (ReviewPhotoReq req : reqs) {
            Integer s = req.getSortOrder();
            batch.add(ReviewPhoto.builder()
                    .url(req.getUrl())
                    .sortOrder(s == null || s <= 0 ? defaultSort : s)
                    .review(review)
                    .build());
            defaultSort++;
        }

        // 정렬값 기준 정리 후 1..n 재부여
        batch.sort(Comparator.comparingInt(ReviewPhoto::getSortOrder));
        for (int i = 0; i < batch.size(); i++) {
            batch.get(i).setSortOrder(i + 1);
        }

        List<ReviewPhoto> saved = reviewPhotoRepository.saveAll(batch);
        return saved.stream().map(ReviewPhotoRes::toReviewPhotoRes).toList();
    }

    // ========= 수정 =========

    @Transactional
    public ReviewPhotoRes update(Long photoId, ReviewPhotoReq req) {
        ReviewPhoto photo = reviewPhotoRepository.findById(photoId)
                .orElseThrow(() -> new RecordNotFoundException("리뷰 사진 없음", "Not Found ReviewPhoto"));

        if (req.getUrl() != null) photo.setUrl(req.getUrl());
        if (req.getSortOrder() != null && req.getSortOrder() > 0) {
            photo.setSortOrder(req.getSortOrder());
            normalizeSort(photo.getReview().getReviewId());
        }

        return ReviewPhotoRes.toReviewPhotoRes(photo);
    }


    // ========= 삭제 =========

    @Transactional
    public void delete(Long photoId) {
        ReviewPhoto photo = reviewPhotoRepository.findById(photoId)
                .orElseThrow(() -> new RecordNotFoundException("리뷰 사진 없습니다", "Not Found reviewPhoto"));
        Long reviewId = photo.getReview().getReviewId();

        reviewPhotoRepository.delete(photo);
        normalizeSort(reviewId);
        log.info("리뷰 사진 삭제: photoId={}, reviewId={}", photoId, reviewId);
    }

    // ========= 내부 유틸 =========

    private Review ensureReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RecordNotFoundException("리뷰 없습니다.","Not Found ReviewPhoto"));
    }

    private void normalizeSort(Long reviewId) {
        List<ReviewPhoto> list = reviewPhotoRepository.findByReview_ReviewIdOrderBySortOrderAsc(reviewId);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSortOrder(i + 1);
        }
        reviewPhotoRepository.saveAll(list);
    }
}
