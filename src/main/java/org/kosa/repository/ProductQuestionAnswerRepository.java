package org.kosa.repository;

import org.kosa.entity.ProductQuestion;
import org.kosa.entity.ProductQuestionAnswer;
import org.kosa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductQuestionAnswerRepository extends JpaRepository<ProductQuestionAnswer, Long> {

    // 질문별 답변 조회
    // List<ProductQuestionAnswer> findByProductQuestion(ProductQuestion question);

    // 답변자별 조회
    List<ProductQuestionAnswer> findByUsers(User user);

    // 질문에 대한 답변 존재 여부
    // boolean existsByProductQuestion(ProductQuestion question);

    // 특정 기간 답변 조회
    List<ProductQuestionAnswer> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 질문과 답변 함께 조회
    // @Query("SELECT pqa FROM ProductQuestionAnswer pqa JOIN FETCH pqa.productQuestion JOIN FETCH pqa.users WHERE pqa.answerId = :id")
    // Optional<ProductQuestionAnswer> findByIdWithDetails(@Param("id") Long id);

    // 질문 ID로 답변 조회
    // @Query("SELECT pqa FROM ProductQuestionAnswer pqa WHERE pqa.productQuestion.questionId = :questionId ORDER BY pqa.createdAt DESC")
    // List<ProductQuestionAnswer> findByQuestionId(@Param("questionId") Long questionId);
}