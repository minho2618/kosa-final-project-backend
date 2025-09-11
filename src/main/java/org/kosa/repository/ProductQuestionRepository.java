package org.kosa.repository;

import org.kosa.entity.Product;
import org.kosa.entity.ProductQuestion;
import org.kosa.entity.Users;
import org.kosa.enums.ProductQuestionsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductQuestionRepository extends JpaRepository<ProductQuestion, Long> {

//    // Product별 질문 조회
//    List<ProductQuestion> findByProduct(Product product);
//    Page<ProductQuestion> findByProduct(Product product, Pageable pageable);
//
//    // User별 질문 조회
//    List<ProductQuestion> findByUser(Users user);
//
//    // 상태별 조회
//    List<ProductQuestion> findByStatus(ProductQuestionsStatus status);
//
//    // 최근 업데이트된 질문
//    List<ProductQuestion> findByUpdatedAtAfter(LocalDateTime date);
//
//    // 답변 대기중인 질문 조회
//    @Query("SELECT pq FROM ProductQuestion pq WHERE pq.status = :status AND pq.product = :product ORDER BY pq.createdAt DESC")
//    List<ProductQuestion> findPendingQuestions(@Param("product") Product product, @Param("status") ProductQuestionsStatus status);
//
//    // 상태 일괄 업데이트
//    @Modifying
//    @Query("UPDATE ProductQuestion pq SET pq.status = :status WHERE pq.questionId IN :ids")
//    int updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") ProductQuestionsStatus status);
//
//    // 연관 엔터티와 함께 조회
//    @Query("SELECT DISTINCT pq FROM ProductQuestion pq LEFT JOIN FETCH pq.user LEFT JOIN FETCH pq.product WHERE pq.questionId = :id")
//    ProductQuestion findByIdWithDetails(@Param("id") Long id);
}