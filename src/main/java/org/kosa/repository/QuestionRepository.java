package org.kosa.repository;

import org.kosa.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 찾기
    Optional<Question> findByQuestionId(Long id);
    List<Question> findByUserId(Long id);
    List<Question> findByTitle(String title);
}
