package org.kosa.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.config.TestConfig;
import org.kosa.entity.Member;
import org.kosa.entity.Product;
import org.kosa.entity.ProductQuestion;
import org.kosa.enums.MemberRole;
import org.kosa.enums.ProductQuestionsStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TestConfig
// @Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProductQuestionRepositoryTest {

    @Autowired
    private ProductQuestionRepository productQuestionRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    private Product testProduct;
    private Member testMember;

    @BeforeEach
    void setUp() {
        productQuestionRepository.deleteAll();
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
                .email("questioner@example.com")
                .password("password")
                .role(MemberRole.ROLE_CUSTOMER)
                .name("Test Questioner")
                .build();
        
        testMember = memberRepository.save(testMember);
    }

    @Test
    @DisplayName("신규 상품 문의 저장 테스트")
    void testSaveProductQuestion() {
        // Given
        ProductQuestion productQuestion = ProductQuestion.builder()
                .product(testProduct)
                .member(testMember)
                .content("This is a question about the product")
                .status(ProductQuestionsStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        // When
        ProductQuestion savedQuestion = productQuestionRepository.save(productQuestion);

        // Then
        assertThat(savedQuestion.getQuestionId()).isNotNull();
        assertThat(savedQuestion.getContent()).isEqualTo("This is a question about the product");
        assertThat(savedQuestion.getProduct()).isEqualTo(testProduct);
        assertThat(savedQuestion.getMember()).isEqualTo(testMember);
        assertThat(savedQuestion.getStatus()).isEqualTo(ProductQuestionsStatus.OPEN);
    }

    @Test
    @DisplayName("상품별 문의 조회 테스트")
    void testFindByProduct() {
        // Given
        ProductQuestion question1 = ProductQuestion.builder()
                .product(testProduct)
                .member(testMember)
                .content("Content 1")
                .status(ProductQuestionsStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();
        
        ProductQuestion question2 = ProductQuestion.builder()
                .product(testProduct)
                .member(testMember)
                .content("Content 2")
                .status(ProductQuestionsStatus.ANSWERED)
                .createdAt(LocalDateTime.now())
                .build();

        productQuestionRepository.save(question1);
        productQuestionRepository.save(question2);

        // When
        List<ProductQuestion> questions = productQuestionRepository.findByProduct(testProduct);

        // Then
        assertThat(questions).hasSize(2);
        assertThat(questions)
                .extracting("content")
                .containsExactlyInAnyOrder("Content 1", "Content 2");
    }

    @Test
    @DisplayName("상품별 문의 페이징 조회 테스트")
    void testFindByProductWithPagination() {
        // Given
        for (int i = 0; i < 5; i++) {
            ProductQuestion question = ProductQuestion.builder()
                    .product(testProduct)
                    .member(testMember)
                    .content("Content " + i)
                    .status(ProductQuestionsStatus.OPEN)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            productQuestionRepository.save(question);
        }

        Pageable pageable = PageRequest.of(0, 3);

        // When
        Page<ProductQuestion> questions = productQuestionRepository.findByProduct(testProduct, pageable);

        // Then
        assertThat(questions).hasSize(3);
        assertThat(questions.getTotalElements()).isEqualTo(5);
    }

    @Test
    @DisplayName("회원별 문의 조회 테스트")
    void testFindByMember() {
        // Given
        ProductQuestion question = ProductQuestion.builder()
                .product(testProduct)
                .member(testMember)
                .content("User content")
                .status(ProductQuestionsStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();

        productQuestionRepository.save(question);

        // When
        List<ProductQuestion> questions = productQuestionRepository.findByMember(testMember);

        // Then
        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getMember().getEmail()).isEqualTo("questioner@example.com");
    }

    @Test
    @DisplayName("상태별 문의 조회 테스트")
    void testFindByStatus() {
        // Given
        ProductQuestion pendingQuestion = ProductQuestion.builder()
                .product(testProduct)
                .member(testMember)
                .content("Pending content")
                .status(ProductQuestionsStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();
        
        ProductQuestion answeredQuestion = ProductQuestion.builder()
                .product(testProduct)
                .member(testMember)
                .content("Answered content")
                .status(ProductQuestionsStatus.ANSWERED)
                .createdAt(LocalDateTime.now())
                .build();

        productQuestionRepository.save(pendingQuestion);
        productQuestionRepository.save(answeredQuestion);

        // When
        List<ProductQuestion> pendingQuestions = productQuestionRepository.findByStatus(ProductQuestionsStatus.OPEN);

        // Then
        assertThat(pendingQuestions).hasSize(1);
        assertThat(pendingQuestions.get(0).getContent()).isEqualTo("Pending content");
    }

    @Test
    @DisplayName("최근 업데이트된 문의 조회 테스트")
    void testFindByUpdatedAtAfter() {
        // Given
        LocalDateTime date = LocalDateTime.now().minusHours(1);
        
        ProductQuestion recentQuestion = ProductQuestion.builder()
                .product(testProduct)
                .member(testMember)
                .content("Recent content")
                .status(ProductQuestionsStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productQuestionRepository.save(recentQuestion);

        // When
        List<ProductQuestion> recentQuestions = productQuestionRepository.findByUpdatedAtAfter(date);

        // Then
        assertThat(recentQuestions).hasSize(1);
        assertThat(recentQuestions.get(0).getContent()).isEqualTo("Recent content");
    }

    @Test
    @DisplayName("상태와 상품으로 대기중인 문의 조회 테스트")
    void testFindPendingQuestions() {
        // Given
        ProductQuestion pendingQuestion = ProductQuestion.builder()
                .product(testProduct)
                .member(testMember)
                .content("Pending content")
                .status(ProductQuestionsStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();
        
        ProductQuestion answeredQuestion = ProductQuestion.builder()
                .product(testProduct)
                .member(testMember)
                .content("Answered content")
                .status(ProductQuestionsStatus.ANSWERED)
                .createdAt(LocalDateTime.now())
                .build();

        productQuestionRepository.save(pendingQuestion);
        productQuestionRepository.save(answeredQuestion);

        // When
        List<ProductQuestion> pendingQuestions = productQuestionRepository.findPendingQuestions(
                testProduct, ProductQuestionsStatus.OPEN);

        // Then
        assertThat(pendingQuestions).hasSize(1);
        assertThat(pendingQuestions.get(0).getContent()).isEqualTo("Pending content");
    }

    @Test
    @DisplayName("상품 문의 업데이트 테스트")
    void testUpdateProductQuestionStatus() {
        // Given
        ProductQuestion question = ProductQuestion.builder()
                .product(testProduct)
                .member(testMember)
                .content("Original content")
                .status(ProductQuestionsStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build();
        
        ProductQuestion savedQuestion = productQuestionRepository.save(question);

        // When
        savedQuestion.setStatus(ProductQuestionsStatus.ANSWERED);
        savedQuestion.setUpdatedAt(LocalDateTime.now());
        ProductQuestion updatedQuestion = productQuestionRepository.save(savedQuestion);

        // Then
        assertThat(updatedQuestion.getStatus()).isEqualTo(ProductQuestionsStatus.ANSWERED);
    }
}