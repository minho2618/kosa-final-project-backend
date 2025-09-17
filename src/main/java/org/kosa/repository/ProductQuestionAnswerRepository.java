package org.kosa.repository;

import org.kosa.entity.Member;
import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductQuestionAnswerRepository extends JpaRepository<ProductQuestionAnswer, Long> {

    // 질문별 답변 조회
    ProductQuestionAnswer findByProductQuestion(ProductQuestion question);

    // 답변자별 조회
    // List<ProductQuestionAnswer> findByMember(Member member);

    // 질문에 대한 답변 존재 여부
    // boolean existsByProductQuestion(ProductQuestion question);

    // 특정 기간 답변 조회
    // List<ProductQuestionAnswer> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 질문과 답변 함께 조회
    // @Query("SELECT pqa FROM ProductQuestionAnswer pqa JOIN FETCH pqa.productQuestion JOIN FETCH pqa.member WHERE pqa.answerId = :id")
    // Optional<ProductQuestionAnswer> findByIdWithDetails(@Param("id") Long id);

    // 질문 ID로 답변 조회
    // @Query("SELECT pqa FROM ProductQuestionAnswer pqa WHERE pqa.productQuestion.questionId = :questionId ORDER BY pqa.createdAt DESC")
    // List<ProductQuestionAnswer> findByQuestionId(@Param("questionId") Long questionId);
}