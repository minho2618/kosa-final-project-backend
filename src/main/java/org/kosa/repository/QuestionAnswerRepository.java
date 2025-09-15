package org.kosa.repository;

import org.kosa.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
    @Query("select qa from QuestionAnswer qa JOIN FETCH qa.question WHERE qa.question.questionId = :questionId")
    List<QuestionAnswer> findByQuestionId(Long questionId);
}
