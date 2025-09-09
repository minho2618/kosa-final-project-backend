package org.kosa.repository;

import org.kosa.entity.Product;
import org.kosa.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 리뷰 작성(BUY-COM-003)

    // 고객 후기 및 평점(BUY-PROD-003)
    Optional<List<Review>> findReviewByProduct(Product product);

}