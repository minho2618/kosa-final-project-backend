package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.config.TestConfig;
import org.kosa.entity.Member;
import org.kosa.entity.Product;
import org.kosa.entity.Review;
import org.kosa.enums.MemberRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestConfig
// @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    private Product testProduct;
    private Member testMember;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        productRepository.deleteAll();
        memberRepository.deleteAll();
        
        testProduct = Product.builder()
                .name("Test Product")
                .description("Test Product Description")
                .price(java.math.BigDecimal.valueOf(99.99))
                .isActive(true)
                .build();
        
        testProduct = productRepository.save(testProduct);

        testMember = Member.builder()
                .email("reviewer@example.com")
                .password("password")
                .role(MemberRole.ROLE_CUSTOMER)
                .name("Test Reviewer")
                .build();
        
        testMember = memberRepository.save(testMember);
    }

    @Test
    @DisplayName("신규 리뷰 저장 테스트")
    void testSaveReview() {
        // Given
        Review review = Review.builder()
                .product(testProduct)
                .member(testMember)
                .rating(5L)
                .content("Great product!")
                .build();

        // When
        Review savedReview = reviewRepository.save(review);

        // Then
        assertThat(savedReview.getReviewId()).isNotNull();
        assertThat(savedReview.getRating()).isEqualTo(5L);
        assertThat(savedReview.getContent()).isEqualTo("Great product!");
        assertThat(savedReview.getProduct()).isEqualTo(testProduct);
        assertThat(savedReview.getMember()).isEqualTo(testMember);
    }

    @Test
    @DisplayName("상품별 리뷰 전체 조회 테스트")
    void testFindAllByProduct() {
        // Given
        Review review1 = Review.builder()
                .product(testProduct)
                .member(testMember)
                .rating(4L)
                .content("Good product")
                .build();
        
        Review review2 = Review.builder()
                .product(testProduct)
                .member(testMember)
                .rating(5L)
                .content("Excellent product!")
                .build();

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        // When
        List<Review> reviews = reviewRepository.findAllByProduct(testProduct);

        // Then
        assertThat(reviews).hasSize(2);
        assertThat(reviews)
                .extracting("rating")
                .containsExactlyInAnyOrder(4, 5);
        
        assertThat(reviews)
                .extracting("comment")
                .containsExactlyInAnyOrder("Good product", "Excellent product!");
    }

    @Test
    @DisplayName("리뷰 업데이트 테스트")
    void testUpdateReview() {
        // Given
        Review review = Review.builder()
                .product(testProduct)
                .member(testMember)
                .rating(3L)
                .content("Average product")
                .createdAt(LocalDateTime.now())
                .build();
        
        Review savedReview = reviewRepository.save(review);

        // When
        savedReview.setRating(5L);
        savedReview.setContent("Actually a great product!");
        Review updatedReview = reviewRepository.save(savedReview);

        // Then
        assertThat(updatedReview.getRating()).isEqualTo(5);
        assertThat(updatedReview.getContent()).isEqualTo("Actually a great product!");
    }

    @Test
    @DisplayName("리뷰 삭제 테스트")
    void testDeleteReview() {
        // Given
        Review review = Review.builder()
                .product(testProduct)
                .member(testMember)
                .rating(4L)
                .content("Good product")
                .createdAt(LocalDateTime.now())
                .build();
        
        Review savedReview = reviewRepository.save(review);

        // When
        reviewRepository.delete(savedReview);

        // Then
        assertThat(reviewRepository.findById(savedReview.getReviewId())).isEmpty();
    }
}