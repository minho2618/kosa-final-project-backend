package org.kosa.repository;

import org.kosa.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // 찾기
    Optional<Question> findByQuestionId(Long id);
    @Query("SELECT q FROM Question q JOIN FETCH q.member WHERE q.member.memberId = :memberId")
    Page<Question> findByMemberId(Long memberId, Pageable pageable);
    @Query("SELECT q FROM Question q JOIN FETCH q.member WHERE q.title LIKE %:title%")
    Page<Question> findByTitle(String title, Pageable pageable);
}
