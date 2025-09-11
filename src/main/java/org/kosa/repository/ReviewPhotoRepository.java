package org.kosa.repository;

import org.kosa.entity.ReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {

    // 특정 리뷰의 모든 사진
    List<ReviewPhoto> findByreivew_id(Long reviewId);

}
