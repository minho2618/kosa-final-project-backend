package org.kosa.repository;

import org.kosa.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // 찾기
    Optional<Question> findByQuestionId(Long id);
    @Query("SELECT q FROM Question q JOIN FETCH q.member WHERE q.member.memberId = :memberId")
    List<Question> findByMemberId(Long memberId);
    @Query("SELECT q FROM Question q JOIN FETCH q.member WHERE q.title LIKE %:title%")
    List<Question> findByTitle(String title);
}
