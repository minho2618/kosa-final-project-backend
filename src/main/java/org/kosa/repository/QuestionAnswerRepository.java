package org.kosa.repository;

import org.kosa.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
    @Query("select q from QuestionAnswer q WHERE q.question.questionId = :questionId")
    List<QuestionAnswer> findByQuestionId(Long questionId);
}
