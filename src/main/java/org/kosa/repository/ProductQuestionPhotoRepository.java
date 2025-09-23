package org.kosa.repository;

import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductQuestionPhotoRepository extends JpaRepository<ProductQuestionPhoto, Long> {
    // 질문별 사진 조회 (정렬순)
    List<ProductQuestionPhoto> findByProductQuestion_QuestionIdOrderBySortOrder(Long productQuestionId);

    // 사진 조회
    Optional<ProductQuestionPhoto> findByPhotoId(Long photoId);

    // 정렬 순서 업데이트
    @Modifying
    @Query("UPDATE ProductQuestionPhoto p SET p.sortOrder = :sortOrder WHERE p.photoId = :photoId")
    int updateSortOrder(@Param("photoId") Long photoId, @Param("sortOrder") int sortOrder);

    void deleteByProductQuestion_QuestionId(Long productQuestionId);

    int countByProductQuestion(ProductQuestion productQuestion);
}