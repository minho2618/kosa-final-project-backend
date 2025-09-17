package org.kosa.repository;

import org.kosa.entity.ReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {

    // 특정 리뷰의 모든 사진 (정렬 포함)
    List<ReviewPhoto> findByReview_ReviewIdOrderBySortOrderAsc(Long reviewId);

    // 대표 이미지(정렬 1순위)
    Optional<ReviewPhoto> findFirstByReview_ReviewIdOrderBySortOrderAsc(Long reviewId);

    // 특정 리뷰의 모든 사진 삭제
    void deleteByReview_ReviewId(Long reviewId);

    // 다음 정렬 계산용
    @Query("select coalesce(max(rp.sortOrder), 0) from ReviewPhoto rp where rp.review.reviewId = :reviewId")
    int findMaxSortOrder(Long reviewId);
}
