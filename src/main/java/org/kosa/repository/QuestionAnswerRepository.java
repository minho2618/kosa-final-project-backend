package org.kosa.repository;

import org.kosa.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
    @Query("select qa from QuestionAnswer qa JOIN FETCH qa.question WHERE qa.question.questionId = :questionId")
    List<QuestionAnswer> findByQuestionId(Long questionId);

    Optional<QuestionAnswer> findByAnswerId(Long answerId);

    @Modifying
    @Query("DELETE FROM QuestionAnswer qa WHERE qa.question.id = :questionId")
    void deleteByQuestionId(@Param("questionId") Long questionId);
}
