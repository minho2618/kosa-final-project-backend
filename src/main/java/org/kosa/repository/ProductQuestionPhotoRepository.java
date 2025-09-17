package org.kosa.repository;

import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductQuestionPhotoRepository extends JpaRepository<ProductQuestionPhoto, Long> {

    // 질문별 사진 조회 (정렬순)
    // List<ProductQuestionPhoto> findByProductQuestionOrderBySortOrder(ProductQuestion question);

    // URL로 조회
    // ProductQuestionPhoto findByUrl(String url);

    // 질문 ID로 사진 삭제
    // @Modifying
    // @Query("DELETE FROM ProductQuestionPhoto p WHERE p.productQuestion.questionId = :questionId")
    // void deleteByQuestionId(@Param("questionId") Long questionId);

    // 정렬 순서 업데이트
    @Modifying
    @Query("UPDATE ProductQuestionPhoto p SET p.sortOrder = :sortOrder WHERE p.photoId = :photoId")
    int updateSortOrder(@Param("photoId") Long photoId, @Param("sortOrder") int sortOrder);

    // 질문별 사진 개수
    // long countByProductQuestion(ProductQuestion question);
}